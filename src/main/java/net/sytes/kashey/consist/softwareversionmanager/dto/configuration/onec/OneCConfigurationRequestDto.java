package net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec;

public record OneCConfigurationRequestDto(
        String configurationName,
        String configurationAlias,
        String currentVersion,
        String latestVersion,
        Long departmentId) {
}
