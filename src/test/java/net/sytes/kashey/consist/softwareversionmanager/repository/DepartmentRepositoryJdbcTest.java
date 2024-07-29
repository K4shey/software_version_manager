package net.sytes.kashey.consist.softwareversionmanager.repository;

import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {"spring.config.location=classpath:application_test.yml"})
class DepartmentRepositoryJdbcTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private DepartmentRepositoryJdbc repository;

    @BeforeEach
    public void setup() {
        repository = new DepartmentRepositoryJdbc(jdbcTemplate);
    }

    @Test
    @DisplayName("Save new department into DB")
    void saveDepartment_DepartmentIsCreated() {

        Department departmentToSave = new Department("IT");
        Department savedDepartment = repository.save(departmentToSave);

        assertThat(savedDepartment).isNotNull();
        assertThat(savedDepartment.getDepartmentId()).isNotNull();
        assertThat(savedDepartment.getDepartmentName()).isEqualTo("IT");

        String sql = "SELECT * FROM departments WHERE department_id = ?";
        Department foundDepartment = jdbcTemplate.queryForObject(sql, new Object[]{savedDepartment.getDepartmentId()},
                new DepartmentRepositoryJdbc.DepartmentRowMapper());

        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.getDepartmentName()).isEqualTo("IT");
    }

    @Test
    @DisplayName("Delete existing department from DB")
    void deleteDepartment_DepartmentIsDeleted() {

        Department departmentToSave = new Department("IT");
        Department savedDepartment = repository.save(departmentToSave);

        repository.delete(savedDepartment);

        String sql = "SELECT COUNT(*) FROM departments WHERE department_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{savedDepartment.getDepartmentId()}, Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Update department name in DB")
    void updateDepartmentName_DepartmentNameIsUpdated() {

        Department departmentToSave = new Department("IT");
        Department savedDepartment = repository.save(departmentToSave);

        savedDepartment.setDepartmentName("HR");
        Department updatedDepartment = repository.save(savedDepartment);

        String sql = "SELECT * FROM departments WHERE department_id = ?";
        Department foundDepartment = jdbcTemplate.queryForObject(sql, new Object[]{savedDepartment.getDepartmentId()},
                new DepartmentRepositoryJdbc.DepartmentRowMapper());

        assertThat(foundDepartment).isNotNull();
        assertThat(foundDepartment.getDepartmentName()).isEqualTo("HR");
    }

    @Test
    @DisplayName("Get department by ID")
    void getDepartmentById_DepartmentIsFound() {

        Department departmentToSave = new Department("IT");
        Department savedDepartment = repository.save(departmentToSave);

        Optional<Department> foundDepartment = repository.findById(savedDepartment.getDepartmentId());
        assertThat(foundDepartment).isPresent();
        assertThat(foundDepartment.get().getDepartmentName()).isEqualTo("IT");
    }

    @Test
    @DisplayName("Get all departments")
    void getAllDepartments_DepartmentsAreFound() {

        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES ('IT')");
        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES ('HR')");

        List<Department> departments = repository.findAll();

        assertThat(departments).isNotEmpty();
        assertThat(departments.size()).isEqualTo(2);
    }
}
