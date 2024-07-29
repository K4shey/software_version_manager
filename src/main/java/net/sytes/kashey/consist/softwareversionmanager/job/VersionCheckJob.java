package net.sytes.kashey.consist.softwareversionmanager.job;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.sytes.kashey.consist.softwareversionmanager.config.SoftwareVendorProperties;
import net.sytes.kashey.consist.softwareversionmanager.service.SoftwareVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Profile("!test")
public class VersionCheckJob {

    private final SoftwareVersionService softwareVersionService;

    private final SoftwareVendorProperties properties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public VersionCheckJob(SoftwareVersionService softwareVersionService, SoftwareVendorProperties properties) {
        this.softwareVersionService = softwareVersionService;
        this.properties = properties;
    }

    @PostConstruct
    public void startScheduler() {
        scheduler.scheduleAtFixedRate(this::checkVersion, 0, properties.checkIntervalInMinutes(), TimeUnit.MINUTES);
    }

    @PreDestroy
    public void stopScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private void checkVersion() {
        softwareVersionService.checkForUpdates();
    }
}