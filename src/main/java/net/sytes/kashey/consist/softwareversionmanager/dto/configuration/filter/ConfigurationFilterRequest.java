package net.sytes.kashey.consist.softwareversionmanager.dto.configuration.filter;

public class ConfigurationFilterRequest {
    private String status;
    private Long departmentId;

    public ConfigurationFilterRequest() {
    }

    public ConfigurationFilterRequest(String status, Long departmentId) {
        this.status = status;
        this.departmentId = departmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
