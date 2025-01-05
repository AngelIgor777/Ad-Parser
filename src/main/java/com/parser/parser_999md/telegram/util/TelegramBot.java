package com.parser.parser_999md.telegram.util;

import com.parser.parser_999md.Entity.Car;
import com.parser.parser_999md.repository.CarRepository;
import com.parser.parser_999md.telegram.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.Optional;


@Slf4j
@Component
@EnableScheduling
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final CarRepository carRepository;


    public TelegramBot(BotConfig botConfig, CarRepository carRepository) {

        this.botConfig = botConfig;
        this.carRepository = carRepository;
        ArrayList<BotCommand> botCommands = new ArrayList<>();
        botCommands.add(new BotCommand("/start", "Запуск бота"));
        botCommands.add(new BotCommand("/help", "Список доступных команд"));
        botCommands.add(new BotCommand("/info", "Информация о боте"));


        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        String botName = botConfig.getBotName();
        return botName;
    }

    @Override
    public String getBotToken() {
        String botKey = botConfig.getBotKey();
        return botKey;
    }

    ArrayList<Long> longs = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            switch (text) {
                case "/start":
                    sendMessage(chatId, "id chat: " + chatId);
                    longs.add(chatId);
                    break;
                case "/help":
                    break;
                case "/register":
                    break;
                case "/info":
                    break;
                case "/menu":
                    break;
                default:
                    break;
            }
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        message.setParseMode("HTML");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(Long chatId, String photoUrl, String caption) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(photoUrl));
        photo.setCaption(caption);

        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhotoWithCaption(Long chatId, String photoUrl, String caption) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setPhoto(new InputFile(photoUrl)); // Укажите URL или InputStream изображения
        photo.setCaption(caption); // Укажите текст, который будет отправлен вместе с фото
        photo.setParseMode("HTML");
        try {
            execute(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "${cron.scheduler}")
    private void sendCar() {
        Optional<Car> carOptional = carRepository.findById(354);

        if (carOptional.isPresent()) {
            Car car = carOptional.get();

            // Формируем текст подписи
            String caption = String.format(
                    "<b>Название:</b> %s\n<b>Пробег</b>: %s\n<b>Ссылка:</b> %s",
                    car.getCarTitle(),
                    car.getMileage(),
                    car.getCarUrl()
            );
            for (Long aLong : longs) {
                sendPhotoWithCaption(aLong, car.getPhotoUrl(), caption);
            }

        }
    }
}
