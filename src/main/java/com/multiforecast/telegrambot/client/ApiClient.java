package com.multiforecast.telegrambot.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiClient {

    private final RestTemplate restTemplate;

    public Optional<String> getForecast(Long userId) {

        var url = "http://apiRouter:9095/forecast?userId={userId}";
        var entity = HttpEntity.EMPTY;
        var params = Map.of("userId", userId);

        log.info("Request entity = {}\n params = {}", entity, params);
        String forecast = null;
        try {
            forecast = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, params).getBody();
        } catch (HttpClientErrorException e) {
            log.error("Error during retrieving forecast", e);
        }

        return Optional.ofNullable(forecast);
    }
}
