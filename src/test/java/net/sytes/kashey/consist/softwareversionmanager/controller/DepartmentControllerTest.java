package net.sytes.kashey.consist.softwareversionmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sytes.kashey.consist.softwareversionmanager.dto.department.DepartmentDto;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.service.DepartmentService;
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

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDepartments() throws Exception {

        List<DepartmentDto> departments = List.of(
                new DepartmentDto("Sales", null, "John Doe", "123456789",
                        "john@example.com", "Note1"),
                new DepartmentDto("HR", null, "Jane Doe", "987654321",
                        "jane@example.com", "Note2")
        );

        when(departmentService.getAllDepartments(any(PageRequest.class))).thenReturn(departments);

        mockMvc.perform(get("/api/v1/departments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(departments)));
    }

    @Test
    void getDepartmentById_Found() throws Exception {

        DepartmentDto dto = new DepartmentDto("Sales", null, "John Doe",
                "123456789", "john@example.com", "Note1");

        when(departmentService.getDepartmentById(anyLong())).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/departments/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    void getDepartmentById_NotFound() throws Exception {

        when(departmentService.getDepartmentById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/departments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDepartment() throws Exception {

        Department department = new Department();
        DepartmentDto responseDto = new DepartmentDto("Sales", null, "John Doe",
                "123456789", "john@example.com", "Note1");

        when(departmentService.createDepartment(any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void updateDepartment() throws Exception {

        Department departmentDetails = new Department();
        DepartmentDto responseDto = new DepartmentDto("Sales", null, "John Doe",
                "123456789", "john@example.com", "Note1");

        when(departmentService.updateDepartment(anyLong(), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDetails)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void deleteDepartment() throws Exception {
        doNothing().when(departmentService).deleteDepartment(anyLong());

        mockMvc.perform(delete("/api/v1/departments/1"))
                .andExpect(status().isNoContent());
    }
}
