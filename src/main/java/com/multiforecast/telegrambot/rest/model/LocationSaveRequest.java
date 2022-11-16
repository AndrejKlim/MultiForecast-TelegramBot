package com.multiforecast.telegrambot.rest.model;

public record LocationSaveRequest(Long userId, String latitude, String longitude) {
}
