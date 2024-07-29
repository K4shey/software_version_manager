package net.sytes.kashey.consist.softwareversionmanager.specification.configuration;

public class StatusSpecification extends ConfigurationSpecification {

    public StatusSpecification(String status) {
        addClause("status = ?", status);
    }
}