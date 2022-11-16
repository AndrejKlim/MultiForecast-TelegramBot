package com.multiforecast.telegrambot.rest;

import com.multiforecast.telegrambot.rest.model.LocationSaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

    @Value("${app.client.router.url.forecastByUserId}")
    private String forecastByUserIdUrl;
    @Value("${app.client.router.url.saveLocation}")
    private String saveLocationUrl;

    public Optional<String> getForecast(Long userId) {

        var entity = HttpEntity.EMPTY;
        var params = Map.of("userId", userId);

        log.info("Get Forecast info. Request entity = {}\n params = {}", entity, params);
        String forecast = null;
        try {
            forecast = restTemplate.exchange(forecastByUserIdUrl, HttpMethod.GET, entity, String.class, params).getBody();
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode().value()) {
                log.error("User not found, reason = ");
                throw new UserNotFoundException();
            }
            log.error("Error during retrieving forecast", e);
        }

        return Optional.ofNullable(forecast);
    }

    public void saveLocation(final LocationSaveRequest locationSaveRequest) {
        var entity = new HttpEntity<>(locationSaveRequest);

        log.info("Save users location. Request entity = {}", entity);

        restTemplate.postForEntity(saveLocationUrl, entity, void.class);
    }
}
