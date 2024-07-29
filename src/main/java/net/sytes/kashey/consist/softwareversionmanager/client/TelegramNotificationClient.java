package net.sytes.kashey.consist.softwareversionmanager.client;

import net.sytes.kashey.consist.softwareversionmanager.config.TelegramProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TelegramNotificationClient implements NotificationClient {

    public static final String API_TELEGRAM_SEND_MESSAGE =
            "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=Markdown";

    private final TelegramProperties telegramProperties;
    private final RestTemplate restTemplate;

    public TelegramNotificationClient(RestTemplateBuilder restTemplateBuilder, TelegramProperties telegramProperties) {
        this.restTemplate = restTemplateBuilder.build();
        this.telegramProperties = telegramProperties;
    }

    public void sendMessage(String message) {
        restTemplate.getForObject(
                String.format(
                        API_TELEGRAM_SEND_MESSAGE,
                        telegramProperties.botToken(),
                        telegramProperties.chatId(),
                        message
                ),
                String.class
        );
    }
}