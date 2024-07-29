package net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec;

import net.sytes.kashey.consist.softwareversionmanager.model.ConfigurationStatus;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;

public record OneCConfigurationDto(
        String configurationAlias,
        String currentVersion,
        String latestVersion,
        Department department,
        ConfigurationStatus status
) {
}
