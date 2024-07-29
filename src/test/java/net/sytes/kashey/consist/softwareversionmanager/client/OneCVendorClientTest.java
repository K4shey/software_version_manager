package net.sytes.kashey.consist.softwareversionmanager.client;

import net.sytes.kashey.consist.softwareversionmanager.config.SoftwareVendorProperties;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.ConfigurationUpdateResponse;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoRequestDto;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OneCVendorClientTest {

    private OneCVendorClient oneCVendorClient;
    private RestTemplate restTemplateMock;

    @BeforeEach
    void setUp() {

        restTemplateMock = mock(RestTemplate.class);
        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplateMock);
        SoftwareVendorProperties vendorPropertiesMock = mock(SoftwareVendorProperties.class);
        when(vendorPropertiesMock.updateUrl()).thenReturn("http://vendor-url.com/update");

        oneCVendorClient = new OneCVendorClient(restTemplateBuilder, vendorPropertiesMock);
    }

    @Test
    void getVersionInfo() {

        OneCVersionInfoRequestDto requestDto = new OneCVersionInfoRequestDto("ARAutomation",
                "2.5.16.115", "1.1.1.1", "", "",
                "NewConfigurationAndOrPlatform");
        ConfigurationUpdateResponse configurationUpdateResponse = new ConfigurationUpdateResponse(
                "2.5.18.41", 522501603,
                "8.3.24.1548",
                "https://dl04.1c.ru/content//AutoUpdatesFiles/ARAutomation/2_5_18_41/82/News1cv8.htm",
                "64d89b6c-8c71-4b4e-8779-8b1d912c405d",
                List.of("64d89b6c-8c71-4b4e-8779-8b1d912c405d", "42348402-7d54-4a8b-a3e1-0df22e7abc25"),
                "f511c1de-0cb2-4a52-a4d1-312c32221837",
                ""
        );
        OneCVersionInfoResponseDto expectedResponseDto = new OneCVersionInfoResponseDto(
                "No Error", "Success", configurationUpdateResponse);
        when(restTemplateMock.postForObject(eq("http://vendor-url.com/update"), eq(requestDto),
                eq(OneCVersionInfoResponseDto.class)))
                .thenReturn(expectedResponseDto);

        OneCVersionInfoResponseDto actualResponseDto = oneCVendorClient.getVersionInfo(requestDto);

        assertEquals(expectedResponseDto, actualResponseDto);
        verify(restTemplateMock, times(1)).postForObject(eq("http://vendor-url.com/update"),
                eq(requestDto), eq(OneCVersionInfoResponseDto.class));
    }
}