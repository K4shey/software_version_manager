package net.sytes.kashey.consist.softwareversionmanager.specification.configuration;

public class DepartmentSpecification extends ConfigurationSpecification {

    public DepartmentSpecification(Long departmentId) {
        addClause("c.department_id = ?", departmentId);
    }
}