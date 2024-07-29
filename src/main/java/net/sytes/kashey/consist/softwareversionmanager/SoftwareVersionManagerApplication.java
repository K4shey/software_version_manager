package net.sytes.kashey.consist.softwareversionmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class SoftwareVersionManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftwareVersionManagerApplication.class, args);
    }

}
