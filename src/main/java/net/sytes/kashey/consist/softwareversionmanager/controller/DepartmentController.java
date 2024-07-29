package net.sytes.kashey.consist.softwareversionmanager.controller;

import net.sytes.kashey.consist.softwareversionmanager.dto.department.DepartmentDto;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentService departmentService;

    @Autowired

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAllDepartments(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size) {
        logger.info("Fetching all departments");
        List<DepartmentDto> departments = departmentService.getAllDepartments(PageRequest.of(page, size));
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        logger.info("Fetching department with id: {}", id);
        return departmentService.getDepartmentById(id)
                .map(department -> {
                    logger.info("Department with id: {} found", id);
                    return ResponseEntity.ok(department);
                })
                .orElseGet(() -> {
                    logger.warn("Department with id: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody Department department) {
        DepartmentDto createdDepartment = departmentService.createDepartment(department);
        logger.info("Created department: {}", createdDepartment.departmentName());
        return ResponseEntity.ok(createdDepartment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable Long id,
                                                          @RequestBody Department departmentDetails) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
        logger.info("Updated department: {}", updatedDepartment.departmentName());
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        logger.info("Deleted department with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}