package net.sytes.kashey.consist.softwareversionmanager.it;

import net.sytes.kashey.consist.softwareversionmanager.client.SoftwareVendorClient;
import net.sytes.kashey.consist.softwareversionmanager.client.TelegramNotificationClient;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.ConfigurationUpdateResponse;
import net.sytes.kashey.consist.softwareversionmanager.dto.configuration.onec.OneCVersionInfoResponseDto;
import net.sytes.kashey.consist.softwareversionmanager.model.Configuration;
import net.sytes.kashey.consist.softwareversionmanager.model.ConfigurationStatus;
import net.sytes.kashey.consist.softwareversionmanager.model.Department;
import net.sytes.kashey.consist.softwareversionmanager.repository.ConfigurationRepository;
import net.sytes.kashey.consist.softwareversionmanager.repository.DepartmentRepository;
import net.sytes.kashey.consist.softwareversionmanager.service.SoftwareVersionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.config.location=classpath:application_test.yml"})
public class SoftwareVersionServiceTest {

    @Autowired
    private SoftwareVersionService softwareVersionService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @MockBean
    private SoftwareVendorClient vendorClient;

    @MockBean
    private TelegramNotificationClient telegramNotifier;

    @Test
    void testCheckForUpdates() {

        Mockito.when(vendorClient.getVersionInfo(any())).thenReturn(
                new OneCVersionInfoResponseDto("No Error", "Success",
                        new ConfigurationUpdateResponse(
                                "2.5.18.41", 522501603,
                                "8.3.24.1548",
                                "https://dl04.1c.ru/content//AutoUpdatesFiles/ARAutomation/2_5_18_41/82/News1cv8.htm",
                                "64d89b6c-8c71-4b4e-8779-8b1d912c405d",
                                List.of("64d89b6c-8c71-4b4e-8779-8b1d912c405d", "42348402-7d54-4a8b-a3e1-0df22e7abc25"),
                                "f511c1de-0cb2-4a52-a4d1-312c32221837",
                                ""
                        )
                )
        );

        Department department = new Department("Бухгалтерия");
        department = departmentRepository.save(department);

        Configuration configuration = new Configuration(
                "ARAutomation",
                "Комплексная автоматизация",
                "2.5.16.115",
                "1.1.1.1",
                department
        );
        configurationRepository.save(configuration);
        softwareVersionService.checkForUpdates();
        Configuration updatedConfig = configurationRepository.findAll().get(0);

        assert updatedConfig.getLatestVersion().equals("2.5.18.41");
        assert updatedConfig.getStatus() == ConfigurationStatus.NEED_UPDATE;
        Mockito.verify(telegramNotifier, Mockito.times(1)).sendMessage(anyString());
    }

    @Test
    void testNoUpdateNeeded() {

        Mockito.when(vendorClient.getVersionInfo(any())).thenReturn(
                new OneCVersionInfoResponseDto("No Error", "Success",
                        new ConfigurationUpdateResponse(
                                "2.5.18.41", 522501603,
                                "8.3.24.1548",
                                "https://dl04.1c.ru/content//AutoUpdatesFiles/ARAutomation/2_5_18_41/82/News1cv8.htm",
                                "64d89b6c-8c71-4b4e-8779-8b1d912c405d",
                                List.of("64d89b6c-8c71-4b4e-8779-8b1d912c405d", "42348402-7d54-4a8b-a3e1-0df22e7abc25"),
                                "f511c1de-0cb2-4a52-a4d1-312c32221837",
                                ""
                        )
                )
        );

        Department department = new Department("Бухгалтерия");
        department = departmentRepository.save(department);

        Configuration configuration = new Configuration(
                "ARAutomation",
                "Комплексная автоматизация",
                "2.5.18.41",
                "2.5.18.41",
                department
        );
        configurationRepository.save(configuration);

        softwareVersionService.checkForUpdates();

        Configuration updatedConfig = configurationRepository.findAll().get(0);

        assert updatedConfig.getStatus() == ConfigurationStatus.ACTUAL;
        Mockito.verify(telegramNotifier, Mockito.times(0)).sendMessage(anyString());
    }

    @Test
    void testCurrentVersionGreaterThanLatest() {

        Mockito.when(vendorClient.getVersionInfo(any())).thenReturn(
                new OneCVersionInfoResponseDto("No Error", "Success",
                        new ConfigurationUpdateResponse(
                                "2.5.18.41", 522501603,
                                "8.3.24.1548",
                                "https://dl04.1c.ru/content//AutoUpdatesFiles/ARAutomation/2_5_18_41/82/News1cv8.htm",
                                "64d89b6c-8c71-4b4e-8779-8b1d912c405d",
                                List.of("64d89b6c-8c71-4b4e-8779-8b1d912c405d", "42348402-7d54-4a8b-a3e1-0df22e7abc25"),
                                "f511c1de-0cb2-4a52-a4d1-312c32221837",
                                ""
                        )
                )
        );

        Department department = new Department("Бухгалтерия");
        department = departmentRepository.save(department);

        Configuration configuration = new Configuration(
                "ARAutomation",
                "Комплексная автоматизация",
                "2.5.18.50",
                "2.5.18.41",
                department
        );

        configurationRepository.save(configuration);
        softwareVersionService.checkForUpdates();

        Configuration updatedConfig = configurationRepository.findAll().get(0);

        assert updatedConfig.getStatus() == ConfigurationStatus.ERROR;
        Mockito.verify(telegramNotifier, Mockito.times(1)).sendMessage(anyString());
    }
}
