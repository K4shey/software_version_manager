package net.sytes.kashey.consist.softwareversionmanager.service;

import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.filter.ConfigurationFilterRequest;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.mapper.ConfigurationMapper;
import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import net.sytes.kashey.consist.softwareversionmanager.model.ConfigurationStatus;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.repository.ConfigurationRepository;
import net.sytes.kashey.consist.softwareversionmanager.repository.DepartmentRepository;
import net.sytes.kashey.consist.softwareversionmanager.specification.configuration.ConfigurationSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConfigurationServiceTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ConfigurationMapper mapper;

    @InjectMocks
    private ConfigurationService configurationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllConfigurations() {

        ConfigurationFilterRequest filterRequest = new ConfigurationFilterRequest();
        filterRequest.setStatus("NEW");
        filterRequest.setDepartmentId(1L);
        List<Configuration> configurations = List.of(new Configuration());
        OneCConfigurationDto dto = new OneCConfigurationDto("alias", "1.0", "1.1",
                new Department(), ConfigurationStatus.NEW);

        when(configurationRepository.findAllWithSpecification(any(ConfigurationSpecification.class), anyInt(), anyInt()))
                .thenReturn(configurations);
        when(mapper.toDto(any(Configuration.class))).thenReturn(dto);

        List<OneCConfigurationDto> result = configurationService.getAllConfigurations(filterRequest, PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
        verify(configurationRepository, times(1)).findAllWithSpecification(any(ConfigurationSpecification.class), anyInt(), anyInt());
        verify(mapper, times(1)).toDto(any(Configuration.class));
    }

    @Test
    void getConfigurationById_Found() {

        Configuration configuration = new Configuration();
        OneCConfigurationDto dto = new OneCConfigurationDto("alias", "1.0", "1.1",
                new Department("IT"), ConfigurationStatus.NEW);

        when(configurationRepository.findById(anyLong())).thenReturn(Optional.of(configuration));
        when(mapper.toDto(any(Configuration.class))).thenReturn(dto);

        Optional<OneCConfigurationDto> result = configurationService.getConfigurationById(1L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
        verify(configurationRepository, times(1)).findById(1L);
        verify(mapper, times(1)).toDto(any(Configuration.class));
    }

    @Test
    void getConfigurationById_NotFound() {

        when(configurationRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<OneCConfigurationDto> result = configurationService.getConfigurationById(1L);

        assertFalse(result.isPresent());
        verify(configurationRepository, times(1)).findById(1L);
    }

    @Test
    void createConfiguration() {

        OneCConfigurationRequestDto request = new OneCConfigurationRequestDto("name",
                "alias", "1.0", "1.1", 1L);
        Department department = new Department();
        Configuration configuration = new Configuration();
        OneCConfigurationDto dto = new OneCConfigurationDto("alias", "1.0", "1.1",
                department, null);

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
        when(configurationRepository.save(any(Configuration.class))).thenReturn(configuration);
        when(mapper.toDto(any(Configuration.class))).thenReturn(dto);

        OneCConfigurationDto result = configurationService.createConfiguration(request);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(departmentRepository, times(1)).findById(1L);
        verify(configurationRepository, times(1)).save(any(Configuration.class));
        verify(mapper, times(1)).toDto(any(Configuration.class));
    }

    @Test
    void updateConfiguration() {
        OneCConfigurationRequestDto request = new OneCConfigurationRequestDto("name",
                "alias", "1.0", "1.1", 1L);
        Department department = new Department();
        Configuration configuration = new Configuration();
        OneCConfigurationDto dto = new OneCConfigurationDto("alias", "1.0",
                "1.1", department, null);

        when(configurationRepository.findById(anyLong())).thenReturn(Optional.of(configuration));
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
        when(configurationRepository.save(any(Configuration.class))).thenReturn(configuration);
        when(mapper.toDto(any(Configuration.class))).thenReturn(dto);

        OneCConfigurationDto result = configurationService.updateConfiguration(1L, request);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(configurationRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).findById(1L);
        verify(configurationRepository, times(1)).save(any(Configuration.class));
        verify(mapper, times(1)).toDto(any(Configuration.class));
    }

    @Test
    void deleteConfiguration() {
        Configuration configuration = new Configuration();

        when(configurationRepository.findById(anyLong())).thenReturn(Optional.of(configuration));
        doNothing().when(configurationRepository).delete(any(Configuration.class));

        configurationService.deleteConfiguration(1L);

        verify(configurationRepository, times(1)).findById(1L);
        verify(configurationRepository, times(1)).delete(any(Configuration.class));
    }

    @Test
    void deleteConfiguration_NotFound() {
        when(configurationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> configurationService.deleteConfiguration(1L));
        verify(configurationRepository, times(1)).findById(1L);
        verify(configurationRepository, never()).delete(any(Configuration.class));
    }

    @Test
    void updateConfiguration_NotFound() {
        OneCConfigurationRequestDto request = new OneCConfigurationRequestDto("name",
                "alias", "1.0", "1.1", 1L);

        when(configurationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> configurationService.updateConfiguration(1L, request));
        verify(configurationRepository, times(1)).findById(1L);
        verify(configurationRepository, never()).save(any(Configuration.class));
        verify(mapper, never()).toDto(any(Configuration.class));
    }

    @Test
    void createConfiguration_DepartmentNotFound() {
        OneCConfigurationRequestDto request = new OneCConfigurationRequestDto("name",
                "alias", "1.0", "1.1", 1L);

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> configurationService.createConfiguration(request));
        verify(departmentRepository, times(1)).findById(1L);
        verify(configurationRepository, never()).save(any(Configuration.class));
        verify(mapper, never()).toDto(any(Configuration.class));
    }
}
