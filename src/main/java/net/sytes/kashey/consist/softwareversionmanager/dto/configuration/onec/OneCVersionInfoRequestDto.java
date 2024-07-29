package net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec;

public record OneCVersionInfoRequestDto(
        String programName,
        String versionNumber,
        String platformVersion,
        String programNewName,
        String redactionNumber,
        String updateType
) {
}