package net.sytes.kashey.consist.softwareversionmanager.client;

import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoResponseDto;

public interface SoftwareVendorClient {
    OneCVersionInfoResponseDto getVersionInfo(OneCVersionInfoRequestDto request);
}
