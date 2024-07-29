package net.sytes.kashey.consist.softwareversionmanager.service;

import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.filter.ConfigurationFilterRequest;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.mapper.ConfigurationMapper;
import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.repository.ConfigurationRepository;
import net.sytes.kashey.consist.softwareversionmanager.repository.DepartmentRepository;
import net.sytes.kashey.consist.softwareversionmanager.specification.configuration.ConfigurationSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    private final ConfigurationRepository configurationRepository;
    private final DepartmentRepository departmentRepository;
    private final ConfigurationMapper mapper;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository,
                                DepartmentRepository departmentRepository, ConfigurationMapper mapper) {
        this.configurationRepository = configurationRepository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    public List<OneCConfigurationDto> getAllConfigurations(ConfigurationFilterRequest filterRequest,
                                                           PageRequest pageRequest) {
        logger.info("Fetching all configurations");
        ConfigurationSpecification spec = buildSpecification(filterRequest);

        int page = pageRequest.getPageNumber();
        int size = pageRequest.getPageSize();
        return configurationRepository.findAllWithSpecification(spec, page, size)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<OneCConfigurationDto> getConfigurationById(Long id) {
        logger.info("Fetching configuration by id: {}", id);
        Optional<Configuration> optionalConfiguration = configurationRepository.findById(id);
        return optionalConfiguration.map(configuration -> {
            logger.info("Configuration found: {}", configuration.getConfigurationName());
            return mapper.toDto(configuration);
        });
    }

    public OneCConfigurationDto createConfiguration(OneCConfigurationRequestDto request) {
        logger.info("Creating new configuration: {}", request.configurationName());

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Configuration newConfiguration = new Configuration(
                request.configurationName(),
                request.configurationAlias(),
                request.currentVersion(),
                request.latestVersion(),
                department
        );

        Configuration savedConfiguration = configurationRepository.save(newConfiguration);
        logger.info("Configuration created with id: {}", savedConfiguration.getConfigurationId());
        return mapper.toDto(savedConfiguration);
    }

    public OneCConfigurationDto updateConfiguration(Long id, OneCConfigurationRequestDto configurationDetails) {
        logger.info("Updating configuration with id: {}", id);
        Configuration configuration = configurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

        configuration.setConfigurationName(configurationDetails.configurationName());
        configuration.setConfigurationAlias(configurationDetails.configurationAlias());
        configuration.setCurrentVersion(configurationDetails.currentVersion());
        configuration.setLatestVersion(configurationDetails.latestVersion());

        Department department = departmentRepository.findById(configurationDetails.departmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        configuration.setDepartment(department);

        Configuration updatedConfiguration = configurationRepository.save(configuration);
        logger.info("Configuration with id: {} updated successfully", id);
        return mapper.toDto(updatedConfiguration);
    }

    public void deleteConfiguration(Long id) {
        logger.info("Deleting configuration with id: {}", id);
        Configuration configuration = configurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));
        configurationRepository.delete(configuration);
        logger.info("Configuration with id: {} deleted successfully", id);
    }

    private ConfigurationSpecification buildSpecification(ConfigurationFilterRequest filterRequest) {
        ConfigurationSpecification spec = new ConfigurationSpecification();

        if (filterRequest.getDepartmentId() != null) {
            spec.addClause("c.department_id = ?", filterRequest.getDepartmentId());
        }

        if (filterRequest.getStatus() != null && !filterRequest.getStatus().isEmpty()) {
            spec.addClause("status = ?", filterRequest.getStatus());
        }

        return spec;
    }
}