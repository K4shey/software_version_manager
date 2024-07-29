package net.sytes.kashey.consist.softwareversionmanager.repository;

import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentRepositoryJdbc implements DepartmentRepository {
    private final JdbcTemplate jdbcTemplate;

    public DepartmentRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Department> findAll(int page, int size) {
        String sql = "SELECT * FROM departments LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{size, page * size}, new DepartmentRowMapper());
    }

    @Override
    public List<Department> findAll() {
        String sql = "SELECT * FROM departments";
        return jdbcTemplate.query(sql, new DepartmentRowMapper());
    }

    @Override
    public Optional<Department> findById(Long id) {
        String sql = "SELECT * FROM departments WHERE department_id = ?";
        List<Department> departments = jdbcTemplate.query(sql, new Object[]{id}, new DepartmentRowMapper());
        return departments.stream().findFirst();
    }

    @Override
    public Department save(Department department) {
        if (department.getDepartmentId() != null && findById(department.getDepartmentId()).isPresent()) {
            updateDepartment(department);
        } else {
            insertDepartment(department);
        }
        return department;
    }

    @Override
    public void delete(Department department) {
        String sql = "DELETE FROM departments WHERE department_id = ?";
        jdbcTemplate.update(sql, department.getDepartmentId());
    }

    private void updateDepartment(Department department) {
        String sql = """
                UPDATE departments 
                SET department_name = ?, 
                    parent_department_id = ?, 
                    head_name = ?, 
                    head_phone = ?, 
                    head_email = ?, 
                    note = ? 
                WHERE department_id = ?
                """;
        jdbcTemplate.update(sql,
                department.getDepartmentName(),
                department.getParentDepartmentId(),
                department.getHeadName(),
                department.getHeadPhone(),
                department.getHeadEmail(),
                department.getNote(),
                department.getDepartmentId());
    }

    private void insertDepartment(Department department) {
        String sql = """
                INSERT INTO departments (department_name, parent_department_id, head_name, head_phone, head_email, note) 
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING department_id
                """;
        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class, department.getDepartmentName(),
                department.getParentDepartmentId(),
                department.getHeadName(),
                department.getHeadPhone(),
                department.getHeadEmail(),
                department.getNote());
        department.setDepartmentId(generatedId);
    }

    private Long getParentDepartmentId(Department parentDepartment) {
        return parentDepartment != null ? parentDepartment.getDepartmentId() : null;
    }

    static class DepartmentRowMapper implements RowMapper<Department> {
        @Override
        public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
            Department department = new Department();
            department.setDepartmentId(rs.getLong("department_id"));
            department.setDepartmentName(rs.getString("department_name"));
            department.setParentDepartmentId(rs.getLong("parent_department_id"));
            department.setHeadName(rs.getString("head_name"));
            department.setHeadPhone(rs.getString("head_phone"));
            department.setHeadEmail(rs.getString("head_email"));
            department.setNote(rs.getString("note"));

            return department;
        }
    }
}