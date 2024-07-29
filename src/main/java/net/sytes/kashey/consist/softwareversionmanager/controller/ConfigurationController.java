package net.sytes.kashey.consist.softwareversionmanager.controller;

import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.filter.ConfigurationFilterRequest;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configurations")
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);
    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public ResponseEntity<List<OneCConfigurationDto>> getAllConfigurations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size) {
        logger.info("Fetching all configurations");
        ConfigurationFilterRequest filterRequest = new ConfigurationFilterRequest(status, departmentId);
        List<OneCConfigurationDto> configurations = configurationService.getAllConfigurations(filterRequest, PageRequest.of(page, size));
        return ResponseEntity.ok(configurations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OneCConfigurationDto> getConfigurationById(@PathVariable Long id) {

        logger.info("Fetching configuration with id: {}", id);
        return configurationService.getConfigurationById(id)
                .map(configuration -> {
                    logger.info("Configuration with id: {} found", id);
                    return ResponseEntity.ok(configuration);
                })
                .orElseGet(() -> {
                    logger.warn("Configuration with id: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<OneCConfigurationDto> createConfiguration(@RequestBody OneCConfigurationRequestDto configurationRequest) {
        logger.info("Creating new configuration: {}", configurationRequest.configurationName());
        OneCConfigurationDto createdConfiguration = configurationService.createConfiguration(configurationRequest);
        logger.info("Configuration created: {}", createdConfiguration.configurationAlias());
        return ResponseEntity.ok(createdConfiguration);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OneCConfigurationDto> updateConfiguration(@PathVariable Long id, @RequestBody OneCConfigurationRequestDto configurationDetails) {
        logger.info("Updating configuration with id: {}", id);
        OneCConfigurationDto updatedConfiguration = configurationService.updateConfiguration(id, configurationDetails);
        logger.info("Configuration with id: {} updated successfully", id);
        return ResponseEntity.ok(updatedConfiguration);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Long id) {
        logger.info("Deleting configuration with id: {}", id);
        configurationService.deleteConfiguration(id);
        logger.info("Configuration with id: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}