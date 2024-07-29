package net.sytes.kashey.consist.softwareversionmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.filter.ConfigurationFilterRequest;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCConfigurationRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.model.ConfigurationStatus;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.service.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigurationController.class)
class ConfigurationControllerTest {

    @MockBean
    private ConfigurationService configurationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllConfigurations() throws Exception {

        ConfigurationFilterRequest filterRequest = new ConfigurationFilterRequest();
        filterRequest.setStatus("NEW");
        filterRequest.setDepartmentId(1L);
        List<OneCConfigurationDto> configurations = List.of(
                new OneCConfigurationDto("Alias1", "1.0", "1.1", new Department(),
                        ConfigurationStatus.NEW),
                new OneCConfigurationDto("Alias2", "2.0", "2.1", new Department(),
                        ConfigurationStatus.NEW)
        );
        when(configurationService.getAllConfigurations(any(ConfigurationFilterRequest.class), any(PageRequest.class)))
                .thenReturn(configurations);

        mockMvc.perform(get("/api/v1/configurations")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(configurations)));
    }


    @Test
    void getConfigurationById_Found() throws Exception {

        OneCConfigurationDto dto = new OneCConfigurationDto("Alias", "1.0", "1.1",
                new Department("IT"), ConfigurationStatus.NEW);

        when(configurationService.getConfigurationById(anyLong())).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/configurations/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getConfigurationById_NotFound() throws Exception {

        when(configurationService.getConfigurationById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/configurations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createConfiguration() throws Exception {

        OneCConfigurationRequestDto requestDto = new OneCConfigurationRequestDto(
                "Config1", "Alias1", "1.0", "1.1", 1L);
        OneCConfigurationDto responseDto = new OneCConfigurationDto("Alias1", "1.0",
                "1.1", new Department("IT"), ConfigurationStatus.NEW);
        when(configurationService.createConfiguration(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void updateConfiguration() throws Exception {

        OneCConfigurationRequestDto requestDto = new OneCConfigurationRequestDto("Config1",
                "Alias1", "1.0", "1.1", 1L);
        OneCConfigurationDto responseDto = new OneCConfigurationDto("Alias1", "1.0",
                "1.1", new Department("IT"), ConfigurationStatus.ACTUAL);
        when(configurationService.updateConfiguration(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/configurations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void deleteConfiguration() throws Exception {

        doNothing().when(configurationService).deleteConfiguration(anyLong());

        mockMvc.perform(delete("/api/v1/configurations/1"))
                .andExpect(status().isNoContent());
    }
}