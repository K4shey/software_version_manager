package net.sytes.kashey.consist.softwareversionmanager.repository;

import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository {

    List<Department> findAll(int page, int size);

    List<Department> findAll();

    Optional<Department> findById(Long id);

    Department save(Department department);

    void delete(Department department);
}
