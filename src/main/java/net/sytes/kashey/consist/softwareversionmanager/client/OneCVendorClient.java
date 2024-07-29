package net.sytes.kashey.consist.softwareversionmanager.client;

import net.sytes.kashey.consist.softwareversionmanager.config.SoftwareVendorProperties;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoResponseDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OneCVendorClient implements SoftwareVendorClient {
    private final RestTemplate restTemplate;
    private final SoftwareVendorProperties vendorProperties;

    public OneCVendorClient(RestTemplateBuilder restTemplateBuilder, SoftwareVendorProperties vendorProperties) {
        this.restTemplate = restTemplateBuilder.build();
        this.vendorProperties = vendorProperties;
    }

    @Override
    public OneCVersionInfoResponseDto getVersionInfo(OneCVersionInfoRequestDto request) {
        return restTemplate.postForObject(vendorProperties.updateUrl(), request, OneCVersionInfoResponseDto.class);
    }
}