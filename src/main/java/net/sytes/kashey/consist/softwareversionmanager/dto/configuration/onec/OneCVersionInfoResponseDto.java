package net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec;

public record OneCVersionInfoResponseDto(
        String errorName,
        String errorMessage,
        ConfigurationUpdateResponse configurationUpdateResponse
) {
}