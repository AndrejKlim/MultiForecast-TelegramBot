package com.multiforecast.telegrambot.bot;

import com.multiforecast.telegrambot.property.BotProperties;
import com.multiforecast.telegrambot.service.BotService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static com.multiforecast.telegrambot.bot.BotCommand.FORECAST;
import static com.multiforecast.telegrambot.bot.BotCommand.SET_LOCATION;

@Component
@Slf4j
public class WeatherBot extends AbilityBot {

    public static final String MARKDOWN = "Markdown";

    private final BotProperties botProperties;
    private final BotService botService;

    public WeatherBot(BotProperties botProperties, BotService botService) {
        super(botProperties.getUsername(), botProperties.getToken());
        this.botProperties = botProperties;
        this.botService = botService;
    }

    @Override
    public long creatorId() {
        return 381058662;
    }

    public Ability menu() {
        return Ability.builder()
                .name("menu")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(mc -> {
                    var locationButton = new InlineKeyboardButton("Установить локацию для прогноза");
                    locationButton.setCallbackData(SET_LOCATION.name());

                    var forecastButton = new InlineKeyboardButton("Ближайший прогноз");
                    forecastButton.setCallbackData(FORECAST.name());

                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                    keyboard.add(List.of(forecastButton));
                    keyboard.add(List.of(locationButton));

                    var keyboardMarkup = new InlineKeyboardMarkup();
                    keyboardMarkup.setKeyboard(keyboard);

                    var sendMessage = new SendMessage(mc.chatId().toString(), "Меню команд");
                    sendMessage.setReplyMarkup(keyboardMarkup);

                    silent.execute(sendMessage);
                })
                .build();
    }

    @Override
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage() && update.getMessage().getLocation() != null) {
            botService.saveLocation(update);
            return;
        }
        if (!update.hasCallbackQuery()) {
            super.onUpdateReceived(update);
            return;
        }

        if (isForecastRequested(update)) {
            getForecast(update);
        }
        if (isLocationSetRequested(update)) {
            getLocationButton(update);
        }
    }


    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    private boolean isForecastRequested(final Update update) {
        return checkBotCommand(update, FORECAST);
    }

    private boolean isLocationSetRequested(final Update update) {
        return checkBotCommand(update, SET_LOCATION);
    }

    private boolean checkBotCommand(final Update update, final BotCommand command) {
        return update.getCallbackQuery().getData().equals(command.name());
    }

    private void getForecast(final Update update) {
        botService.getForecast(update).stream()
                .map(s -> new SendMessage(getChatId(update), s))
                .forEach(message -> silent.execute(message));
    }

    private void getLocationButton(final Update update) {

        var locationButton = new KeyboardButton("Отправить мою геолокацию и сохранить ее для получения прогноза.");
        locationButton.setRequestLocation(true);

        var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(List.of(new KeyboardRow(List.of(locationButton))));

        var message = new SendMessage(getChatId(update), "Geolocation");
        message.setReplyMarkup(keyboardMarkup);

        silent.execute(message);
    }

    @NotNull
    private String getChatId(final Update update) {
        return update.getCallbackQuery().getMessage().getChatId().toString();
    }
}
