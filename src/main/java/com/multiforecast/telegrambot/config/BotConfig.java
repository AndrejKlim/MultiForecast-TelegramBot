package com.multiforecast.telegrambot.config;

import com.multiforecast.telegrambot.property.BotProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class BotConfig {

    private final List<BotSession> botSessions = new ArrayList<>();
    private final List<LongPollingBot> pollingBots;

    public BotConfig(List<LongPollingBot> pollingBots) {
        this.pollingBots = pollingBots;
    }

    @PostConstruct
    public void start() throws TelegramApiException {
        log.info("Starting auto config for telegram bots");
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        pollingBots.forEach(bot -> {
            try {
                log.info("Registering polling bot: {}", bot.getBotUsername());
                botSessions.add(api.registerBot(bot));
            } catch (TelegramApiException e) {
                log.error("Failed to register bot {} due to error", bot.getBotUsername(), e);
            }
        });
    }

    @PreDestroy
    public void stop() {
        botSessions.forEach(session -> {
            if (session != null) {
                session.stop();
            }
        });
    }
}
