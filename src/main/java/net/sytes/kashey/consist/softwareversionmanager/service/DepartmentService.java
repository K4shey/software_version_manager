package net.sytes.kashey.consist.softwareversionmanager.service;

import net.sytes.kashey.consist.softwareversionmanager.dto.department.DepartmentDto;
import net.sytes.kashey.consist.softwareversionmanager.mapper.DepartmentMapper;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper mapper;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper mapper) {
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    public List<DepartmentDto> getAllDepartments(PageRequest pageRequest) {
        logger.info("Fetching all departments");
        List<Department> departments = departmentRepository.findAll(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return departments.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<DepartmentDto> getDepartmentById(Long id) {
        logger.info("Fetching department by id: {}", id);
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        return optionalDepartment.map(department -> {
            logger.info("Department found: {}", department.getDepartmentName());
            return mapper.toDto(department);
        });
    }

    public DepartmentDto createDepartment(Department department) {
        logger.info("Creating new department: {}", department.getDepartmentName());
        Department savedDepartment = departmentRepository.save(department);
        return mapper.toDto(savedDepartment);
    }

    public DepartmentDto updateDepartment(Long id, Department departmentDetails) {
        logger.info("Updating department with id: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        department.setDepartmentName(departmentDetails.getDepartmentName());
        department.setParentDepartmentId(departmentDetails.getParentDepartmentId());
        department.setHeadName(departmentDetails.getHeadName());
        department.setHeadPhone(departmentDetails.getHeadPhone());
        department.setHeadEmail(departmentDetails.getHeadEmail());
        department.setNote(departmentDetails.getNote());
        Department updatedDepartment = departmentRepository.save(department);
        return mapper.toDto(updatedDepartment);
    }

    public void deleteDepartment(Long id) {
        logger.info("Deleting department with id: {}", id);
        Department department = departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
        departmentRepository.delete(department);
    }
}