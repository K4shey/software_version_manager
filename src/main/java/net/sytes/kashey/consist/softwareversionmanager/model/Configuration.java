package net.sytes.kashey.consist.softwareversionmanager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configurations")
public class Configuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long configurationId;

    @Column(nullable = false)
    private String configurationName;

    @Column(nullable = false)
    private String configurationAlias;

    private String currentVersion;
    private String latestVersion;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    private ConfigurationStatus status;

    public Configuration(String configurationName, String configurationAlias, String currentVersion, String latestVersion,
                         Department department) {
        this.configurationName = configurationName;
        this.configurationAlias = configurationAlias;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
        this.department = department;
        this.status = ConfigurationStatus.NEW;
    }

    public Configuration() {
    }

    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    public Long getConfigurationId() {
        return configurationId;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ConfigurationStatus getStatus() {
        return status;
    }

    public void setStatus(ConfigurationStatus status) {
        this.status = status;
    }

    public String getConfigurationAlias() {
        return configurationAlias;
    }

    public void setConfigurationAlias(String configurationAlias) {
        this.configurationAlias = configurationAlias;
    }
}