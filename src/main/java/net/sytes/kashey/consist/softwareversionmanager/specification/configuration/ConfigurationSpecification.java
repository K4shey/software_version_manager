package net.sytes.kashey.consist.softwareversionmanager.specification.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationSpecification {

    private final List<Object> params = new ArrayList<>();
    private final List<String> clauses = new ArrayList<>();

    public void addClause(String clause, Object... args) {
        clauses.add(clause);
        params.addAll(Arrays.asList(args));
    }

    public Object[] getParameters() {
        return params.toArray();
    }

    public String toSqlClause() {
        return String.join(" AND ", clauses);
    }
}