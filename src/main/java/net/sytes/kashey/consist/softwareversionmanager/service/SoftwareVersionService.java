package net.sytes.kashey.consist.softwareversionmanager.service;


import net.sytes.kashey.consist.softwareversionmanager.client.SoftwareVendorClient;
import net.sytes.kashey.consist.softwareversionmanager.client.TelegramNotificationClient;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.ConfigurationUpdateResponse;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoResponseDto;
import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import net.sytes.kashey.consist.softwareversionmanager.model.ConfigurationStatus;
import net.sytes.kashey.consist.softwareversionmanager.repository.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SoftwareVersionService {
    private static final Logger logger = LoggerFactory.getLogger(SoftwareVersionService.class);
    private final SoftwareVendorClient vendorClient;
    private final ConfigurationRepository configurationRepository;

    private final TelegramNotificationClient telegramNotifier;

    @Autowired
    public SoftwareVersionService(SoftwareVendorClient vendorClient, ConfigurationRepository configurationRepository,
                                  TelegramNotificationClient telegramNotifier) {
        this.vendorClient = vendorClient;
        this.configurationRepository = configurationRepository;
        this.telegramNotifier = telegramNotifier;
    }

    @Transactional
    public void checkForUpdates() {
        logger.info("Starting version check for all configurations.");
        configurationRepository.findAll().forEach(this::checkAndUpdateConfiguration);
    }

    private void checkAndUpdateConfiguration(Configuration configuration) {
        logger.info("Checking configuration: {}", configuration.getConfigurationName());

        OneCVersionInfoResponseDto response;
        try {
            response = vendorClient.getVersionInfo(createVersionInfoRequest(configuration));
        } catch (Exception e) {
            logger.error("Failed to get version info for configuration: {}", configuration.getConfigurationName(), e);
            return;
        }

        updateConfigurationIfNeeded(configuration, response);
    }

    private OneCVersionInfoRequestDto createVersionInfoRequest(Configuration configuration) {
        return new OneCVersionInfoRequestDto(
                configuration.getConfigurationName(),
                configuration.getCurrentVersion(),
                "1.1.1.1",
                "",
                "",
                "NewConfigurationAndOrPlatform"
        );
    }

    private void updateConfigurationIfNeeded(Configuration configuration, OneCVersionInfoResponseDto response) {

        ConfigurationUpdateResponse configurationInfo = response.configurationUpdateResponse();
        String latestVersion = configurationInfo.configurationVersion();

        int compareVersionsResult = compareVersions(configuration.getCurrentVersion(), latestVersion);

        switch (compareVersionsResult) {
            case -1:
                handleNewVersionAvailable(configuration, configurationInfo, latestVersion);
                break;
            case 0:
                handleSameVersion(configuration, latestVersion);
                break;
            case 1:
                handleCurrentVersionGreater(configuration, configurationInfo, latestVersion);
        }

        configurationRepository.save(configuration);
        logger.info("Version info for configuration {} updated and saved.", configuration.getConfigurationName());
    }

    private void handleNewVersionAvailable(Configuration configuration, ConfigurationUpdateResponse configurationInfo,
                                           String latestVersion) {
        logger.info("New version available for {}: {}", configuration.getConfigurationName(), latestVersion);
        updateConfigurationStatus(configuration, ConfigurationStatus.NEED_UPDATE, latestVersion);
        newVersionAvailableNotification(configuration, configurationInfo);
    }

    private void handleSameVersion(Configuration configuration, String latestVersion) {
        logger.info("Versions are the same: {}. No update needed", latestVersion);
        updateConfigurationStatus(configuration, ConfigurationStatus.ACTUAL, latestVersion);
    }

    private void handleCurrentVersionGreater(Configuration configuration, ConfigurationUpdateResponse configurationInfo,
                                             String latestVersion) {
        logger.info("Current version {} greater than latest {} for {}: ", configuration.getCurrentVersion(),
                configuration.getConfigurationName(), latestVersion);
        updateConfigurationStatus(configuration, ConfigurationStatus.ERROR, latestVersion);
        CurrentVersionGreaterNotification(configuration, configurationInfo);
    }

    private void updateConfigurationStatus(Configuration configuration, ConfigurationStatus status, String latestVersion) {
        if (!configuration.getStatus().equals(status)) {
            configuration.setStatus(status);
        }
        if (!configuration.getLatestVersion().equals(latestVersion)) {
            configuration.setLatestVersion(latestVersion);
        }
    }

    private void newVersionAvailableNotification(Configuration configuration,
                                                 ConfigurationUpdateResponse configurationInfo) {
        String message = String.format(
                "New version of configuration '%s' available: [%s](%s)",
                configuration.getConfigurationAlias(),
                configuration.getLatestVersion(),
                configurationInfo.updateInfoUrl());

        telegramNotifier.sendMessage(message);
        logger.info("Version info for configuration {} sent to Administrator.", configuration.getConfigurationName());
    }

    private void CurrentVersionGreaterNotification(Configuration configuration,
                                                   ConfigurationUpdateResponse configurationInfo) {
        String message = String.format(
                "Attention! Current version %s greater than latest %s for configuration '%s'",
                configuration.getCurrentVersion(),
                configuration.getLatestVersion(),
                configuration.getConfigurationAlias()
        );

        telegramNotifier.sendMessage(message);
        logger.info("Alert for configuration {} sent to Administrator.", configuration.getConfigurationName());
    }

    private int compareVersions(String currentVersion, String latestVersion) {

        String[] currentVersionArray = currentVersion.split("\\.");
        String[] latestVersionArray = latestVersion.split("\\.");
        int maxLength = Math.max(currentVersionArray.length, latestVersionArray.length);

        for (int i = 0; i < maxLength; i++) {
            int currentVersionNumber = i < currentVersionArray.length ? Integer.parseInt(currentVersionArray[i]) : 0;
            int latestVersionNumber = i < latestVersionArray.length ? Integer.parseInt(latestVersionArray[i]) : 0;

            int comparison = Integer.compare(currentVersionNumber, latestVersionNumber);
            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }
}