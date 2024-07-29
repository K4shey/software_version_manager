package net.sytes.kashey.consist.softwareversionmanager.repository;

import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
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
class ConfigurationRepositoryJdbcTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ConfigurationRepositoryJdbc repository;

    @BeforeEach
    public void setup() {
        repository = new ConfigurationRepositoryJdbc(jdbcTemplate);
    }

    @Test
    @DisplayName("Save new configuration into DB")
    void saveConfiguration_ConfigurationIsCreated() {

        Department department = new Department("IT");
        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES (?)", department.getDepartmentName());
        Long departmentId = jdbcTemplate.queryForObject("SELECT department_id FROM departments WHERE department_name = ?",
                Long.class, department.getDepartmentName());
        department.setDepartmentId(departmentId);

        Configuration configurationToSave = new Configuration("TestConfig", "TestAlias",
                "1.0", "1.0", department);

        Configuration savedConfiguration = repository.save(configurationToSave);

        assertThat(savedConfiguration).isNotNull();
        assertThat(savedConfiguration.getConfigurationId()).isNotNull();
        assertThat(savedConfiguration.getConfigurationName()).isEqualTo("TestConfig");

        String sql = "SELECT * FROM configurations WHERE configuration_id = ?";
        Configuration foundConfiguration = jdbcTemplate.queryForObject(sql,
                new Object[]{savedConfiguration.getConfigurationId()},
                new ConfigurationRepositoryJdbc.ConfigurationRowMapper());

        assertThat(foundConfiguration).isNotNull();
        assertThat(foundConfiguration.getConfigurationName()).isEqualTo("TestConfig");
    }

    @Test
    @DisplayName("Delete existing configuration from DB")
    void deleteConfiguration_ConfigurationIsDeleted() {

        Department department = new Department("IT");
        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES (?)", department.getDepartmentName());
        Long departmentId = jdbcTemplate.queryForObject("SELECT department_id FROM departments WHERE department_name = ?",
                Long.class, department.getDepartmentName());
        department.setDepartmentId(departmentId);

        Configuration configurationToSave = new Configuration("TestConfig", "TestAlias",
                "1.0", "1.0", department);
        Configuration savedConfiguration = repository.save(configurationToSave);

        repository.delete(savedConfiguration);

        String sql = "SELECT COUNT(*) FROM configurations WHERE configuration_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{savedConfiguration.getConfigurationId()},
                Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Update configuration in DB")
    void updateConfiguration_ConfigurationIsUpdated() {

        Department department = new Department("IT");
        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES (?)", department.getDepartmentName());
        Long departmentId = jdbcTemplate.queryForObject("SELECT department_id FROM departments WHERE department_name = ?",
                Long.class, department.getDepartmentName());
        department.setDepartmentId(departmentId);

        Configuration configurationToSave = new Configuration("TestConfig", "TestAlias",
                "1.0", "1.0", department);
        Configuration savedConfiguration = repository.save(configurationToSave);

        savedConfiguration.setConfigurationName("UpdatedConfig");
        Configuration updatedConfiguration = repository.save(savedConfiguration);

        String sql = "SELECT * FROM configurations WHERE configuration_id = ?";
        Configuration foundConfiguration = jdbcTemplate.queryForObject(sql,
                new Object[]{savedConfiguration.getConfigurationId()},
                new ConfigurationRepositoryJdbc.ConfigurationRowMapper());

        assertThat(foundConfiguration).isNotNull();
        assertThat(foundConfiguration.getConfigurationName()).isEqualTo("UpdatedConfig");
    }

    @Test
    @DisplayName("Get configuration by ID")
    void getConfigurationById_ConfigurationIsFound() {

        Department department = new Department("IT");
        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES (?)", department.getDepartmentName());
        Long departmentId = jdbcTemplate.queryForObject("SELECT department_id FROM departments WHERE department_name = ?",
                Long.class, department.getDepartmentName());
        department.setDepartmentId(departmentId);

        Configuration configurationToSave = new Configuration("TestConfig", "TestAlias",
                "1.0", "1.0", department);
        Configuration savedConfiguration = repository.save(configurationToSave);

        Optional<Configuration> foundConfiguration = repository.findById(savedConfiguration.getConfigurationId());
        assertThat(foundConfiguration).isPresent();
        assertThat(foundConfiguration.get().getConfigurationName()).isEqualTo("TestConfig");
    }

    @Test
    @DisplayName("Get all configurations")
    void getAllConfigurations_ConfigurationsAreFound() {

        Department department = new Department("IT");
        jdbcTemplate.update("INSERT INTO departments (department_name) VALUES (?)", department.getDepartmentName());
        Long departmentId = jdbcTemplate.queryForObject("SELECT department_id FROM departments WHERE department_name = ?",
                Long.class, department.getDepartmentName());
        department.setDepartmentId(departmentId);

        jdbcTemplate.update("INSERT INTO configurations (configuration_name, configuration_alias," +
                            " current_version, latest_version, department_id, status) VALUES (?, ?, ?, ?, ?, ?)",
                "Config1", "Alias1", "1.0", "1.0", departmentId, "ACTUAL");
        jdbcTemplate.update("INSERT INTO configurations (configuration_name, configuration_alias," +
                            " current_version, latest_version, department_id, status) VALUES (?, ?, ?, ?, ?, ?)",
                "Config2", "Alias2", "2.0", "2.0", departmentId, "ACTUAL");

        List<Configuration> configurations = repository.findAll();

        assertThat(configurations).isNotEmpty();
        assertThat(configurations.size()).isEqualTo(2);
    }
}
