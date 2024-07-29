package net.sytes.kashey.consist.softwareversionmanager.repository;

import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import net.sytes.kashey.consist.softwareversionmanager.model.ConfigurationStatus;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.specification.configuration.ConfigurationSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ConfigurationRepositoryJdbc implements ConfigurationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationRepositoryJdbc.class);
    private final JdbcTemplate jdbcTemplate;

    public ConfigurationRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Configuration> findAll() {
        String sql = "SELECT * FROM configurations";
        return jdbcTemplate.query(sql, new ConfigurationRowMapper());
    }

    @Override
    public List<Configuration> findAllWithSpecification(ConfigurationSpecification spec, int page, int size) {

        String baseQuery = """
                SELECT c.configuration_id, c.configuration_name, c.configuration_alias, c.current_version, 
                       c.latest_version, c.department_id, c.status, 
                       d.department_id as dep_id, d.department_name as dep_name
                FROM configurations c
                LEFT JOIN departments d ON c.department_id = d.department_id
                """;
        String whereClause = spec.toSqlClause();
        String limitClause = " LIMIT ? OFFSET ?";

        String sql = baseQuery + (whereClause.isEmpty() ? "" : " WHERE " + whereClause) + limitClause;

        Object[] params = spec.getParameters();
        Object[] allParams = new Object[params.length + 2];
        System.arraycopy(params, 0, allParams, 0, params.length);
        allParams[params.length] = size;
        allParams[params.length + 1] = page * size;

        return jdbcTemplate.query(sql, allParams, new ConfigurationRowMapper());
    }

    @Override
    public Optional<Configuration> findById(Long id) {
        String sql = "SELECT * FROM configurations WHERE configuration_id = ?";
        List<Configuration> configurations = jdbcTemplate.query(sql, new Object[]{id}, new ConfigurationRowMapper());
        return configurations.stream().findFirst();
    }

    @Override
    @Transactional
    public Configuration save(Configuration configuration) {

        if (configuration.getConfigurationId() != null && findById(configuration.getConfigurationId()).isPresent()) {
            String sql = """
                    UPDATE configurations 
                    SET configuration_name = ?, 
                        configuration_alias = ?, 
                        current_version = ?, 
                        latest_version = ?, 
                        status = ?, 
                        department_id = ?
                    WHERE configuration_id = ?
                    """;
            int rowsAffected = jdbcTemplate.update(sql,
                    configuration.getConfigurationName(),
                    configuration.getConfigurationAlias(),
                    configuration.getCurrentVersion(),
                    configuration.getLatestVersion(),
                    configuration.getStatus().toString(),
                    configuration.getDepartment().getDepartmentId(),
                    configuration.getConfigurationId());

            if (rowsAffected == 0) {
                logger.warn("No rows were updated for configuration ID: {}", configuration.getConfigurationId());
            }
        } else {
            String sql = """
                    INSERT INTO configurations (configuration_name, configuration_alias, current_version, latest_version, status, department_id) 
                    VALUES (?, ?, ?, ?, ?, ?)
                    RETURNING configuration_id
                    """;
            Long generatedId = jdbcTemplate.queryForObject(sql,
                    new Object[]{
                            configuration.getConfigurationName(),
                            configuration.getConfigurationAlias(),
                            configuration.getCurrentVersion(),
                            configuration.getLatestVersion(),
                            configuration.getStatus().toString(),
                            configuration.getDepartment().getDepartmentId()},
                    Long.class);
            configuration.setConfigurationId(generatedId);
        }
        return configuration;
    }

    @Override
    public void delete(Configuration configuration) {
        String sql = "DELETE FROM configurations WHERE configuration_id = ?";
        jdbcTemplate.update(sql, configuration.getConfigurationId());
    }

    static class ConfigurationRowMapper implements RowMapper<Configuration> {
        @Override
        public Configuration mapRow(ResultSet rs, int rowNum) throws SQLException {

            Configuration configuration = new Configuration();
            configuration.setConfigurationId(rs.getLong("configuration_id"));
            configuration.setConfigurationName(rs.getString("configuration_name"));
            configuration.setConfigurationAlias(rs.getString("configuration_alias"));
            configuration.setCurrentVersion(rs.getString("current_version"));
            configuration.setLatestVersion(rs.getString("latest_version"));
            configuration.setStatus(ConfigurationStatus.valueOf(rs.getString("status")));

            Department department = new Department();
            department.setDepartmentId(rs.getLong("department_id"));
            configuration.setDepartment(department);

            return configuration;
        }
    }
}