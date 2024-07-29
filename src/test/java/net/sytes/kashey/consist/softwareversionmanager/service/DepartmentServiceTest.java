package net.sytes.kashey.consist.softwareversionmanager.service;

import net.sytes.kashey.consist.softwareversionmanager.dto.department.DepartmentDto;
import net.sytes.kashey.consist.softwareversionmanager.mapper.DepartmentMapper;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper mapper;

    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllDepartments() {
        Department department = new Department();
        department.setDepartmentName("IT");
        List<Department> departments = Collections.singletonList(department);
        DepartmentDto dto = new DepartmentDto("IT", null, "Name", "Phone", "Email", "");

        when(departmentRepository.findAll(anyInt(), anyInt())).thenReturn(departments);
        when(mapper.toDto(any(Department.class))).thenReturn(dto);

        List<DepartmentDto> result = departmentService.getAllDepartments(PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
        verify(departmentRepository, times(1)).findAll(anyInt(), anyInt());
        verify(mapper, times(1)).toDto(any(Department.class));
    }

    @Test
    void getDepartmentById_Found() {
        Department department = new Department();
        DepartmentDto dto = new DepartmentDto("name", null, "Name",
                "Phone", "Email", "");

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
        when(mapper.toDto(any(Department.class))).thenReturn(dto);

        Optional<DepartmentDto> result = departmentService.getDepartmentById(1L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
        verify(departmentRepository, times(1)).findById(1L);
        verify(mapper, times(1)).toDto(any(Department.class));
    }

    @Test
    void getDepartmentById_NotFound() {
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<DepartmentDto> result = departmentService.getDepartmentById(1L);

        assertFalse(result.isPresent());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void createDepartment() {
        Department department = new Department();
        DepartmentDto dto = new DepartmentDto("IT", null, "Name",
                "Phone", "Email", "");

        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(mapper.toDto(any(Department.class))).thenReturn(dto);

        DepartmentDto result = departmentService.createDepartment(department);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(departmentRepository, times(1)).save(any(Department.class));
        verify(mapper, times(1)).toDto(any(Department.class));
    }

    @Test
    void updateDepartment() {
        Department department = new Department();
        DepartmentDto dto = new DepartmentDto("IT", null, "Name",
                "Phone", "Email", "");

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(mapper.toDto(any(Department.class))).thenReturn(dto);

        DepartmentDto result = departmentService.updateDepartment(1L, department);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(any(Department.class));
        verify(mapper, times(1)).toDto(any(Department.class));
    }

    @Test
    void updateDepartment_NotFound() {
        Department department = new Department();

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.updateDepartment(1L, department));
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, never()).save(any(Department.class));
        verify(mapper, never()).toDto(any(Department.class));
    }

    @Test
    void deleteDepartment() {
        Department department = new Department();

        when(departmentRepository.findById(anyLong())).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).delete(any(Department.class));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).delete(any(Department.class));
    }

    @Test
    void deleteDepartment_NotFound() {
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, never()).delete(any(Department.class));
    }
}
