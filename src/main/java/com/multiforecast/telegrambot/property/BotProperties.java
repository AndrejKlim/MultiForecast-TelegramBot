package com.multiforecast.telegrambot.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.bot")
@Getter
@Setter
public class BotProperties {
    private String username;
    private String token;
}
