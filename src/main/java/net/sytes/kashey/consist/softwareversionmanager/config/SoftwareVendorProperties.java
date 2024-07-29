package net.sytes.kashey.consist.softwareversionmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vendor")
public record SoftwareVendorProperties(String updateUrl, Integer checkIntervalInMinutes) {
}
