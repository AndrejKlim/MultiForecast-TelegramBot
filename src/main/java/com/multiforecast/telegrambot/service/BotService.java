package com.multiforecast.telegrambot.service;

import com.multiforecast.telegrambot.rest.ApiClient;
import com.multiforecast.telegrambot.rest.UserNotFoundException;
import com.multiforecast.telegrambot.rest.model.LocationSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotService {

    private static final Integer TELEGRAM_MAX_MESSAGE_LENGTH = 4096;

    @Value("${app.bot.response.userNotFoundMessage}")
    private String userNotFoundMessage;

    private final ApiClient apiClient;
    private final DecimalFormat coordinatesSaveFormat = new DecimalFormat("0.00");

    public void saveLocation(final Update update) {
        Location tgLocationResponse = update.getMessage().getLocation();
        apiClient.saveLocation(new LocationSaveRequest(update.getMessage().getFrom().getId(),
                coordinatesSaveFormat.format(tgLocationResponse.getLatitude()),
                coordinatesSaveFormat.format(tgLocationResponse.getLongitude())));
    }

    public List<String> getForecast(final Update update) {
        Optional<String> forecast;
        try {
            forecast = apiClient.getForecast(update.getCallbackQuery().getFrom().getId());
        } catch (final UserNotFoundException e) {
            return List.of(userNotFoundMessage);
        }
        return forecast
                .map(s -> {
                    if (s.length() >= TELEGRAM_MAX_MESSAGE_LENGTH) {
                        return s.substring(0, TELEGRAM_MAX_MESSAGE_LENGTH);
                    }
                    return s;
                }).stream().toList();
    }
}
