package com.multiforecast.telegrambot.service;

import com.multiforecast.telegrambot.client.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService {

    private final ApiClient apiClient;

    public void saveLocation(final Update update) {

    }

    public List<String> getForecast(final Update update) {
//        return apiClient.getForecast(update.getCallbackQuery().getFrom().getId());
        return apiClient.getForecast(1L).map(s -> {
            if (s.length() > 4095) {
                return s.substring(0, 4096);
            }
            return s;
        }).stream().toList();
    }
}
