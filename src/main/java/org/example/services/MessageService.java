package org.example.services;

import org.example.bot.TelegramBot;
import org.example.models.User;
import org.example.utils.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageService {
    private TelegramBot bot;

    public MessageService(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    public void sendStatistics(Long chatId, User user) {
        String stats = "📊 Статистика пользователя " + user.getName() + ":\n" +
                "👤 ID: " + user.getId() + "\n" +
                "📈 Всего дел: " + user.getTotalEvents();
        sendMessage(chatId, stats);
    }

    public void sendMonthButtons(Long chatId) {
        InlineKeyboardMarkup markup = KeyboardFactory.createMonthKeyboard();

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите месяц:");
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDayButtons(Long chatId, int year, int month) {
        int daysInMonth = new org.example.models.Month(month).getDaysInMonth();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 1; i <= daysInMonth; i += 7) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + 7 && j <= daysInMonth; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(String.valueOf(j));
                button.setCallbackData(String.valueOf(j));
                row.add(button);
            }
            rows.add(row);
        }

        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите день:");
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendZodiacButtons(Long chatId) {
        Map<String, String> zodiacSigns = HoroscopeService.getZodiacSigns();
        InlineKeyboardMarkup markup = KeyboardFactory.createZodiacKeyboard(zodiacSigns);

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("♈ Выберите ваш знак зодиака:");
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendHoroscope(Long chatId, String zodiacSign) {
        HoroscopeService horoscopeService = new HoroscopeService();
        String horoscope = horoscopeService.getHoroscope(zodiacSign);
        String zodiacName = horoscopeService.getZodiacName(zodiacSign);

        String messageText = "♊ " + zodiacName + "\n" + horoscope +
                "\n\n✨ Хотите посмотреть другой гороскоп? Используйте команду /horoscope";

        sendMessage(chatId, messageText);
    }
    public void sendWeatherButtons(Long chatId) {
        InlineKeyboardMarkup markup = KeyboardFactory.createWeatherKeyboard();
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("🌤️ Выберите город:");
        message.setReplyMarkup(markup);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendWeather(Long chatId, String city) {
        WeatherService weatherService = new WeatherService();
        String weather = weatherService.getWeather(city);
        sendMessage(chatId, weather);
    }


    public void sendMainMenu(Long chatId, User user) {
        String menu = "🎯 Главное меню\n" +
                "👤 Текущий пользователь: " + user.getName() +
                " (ID: " + user.getId() + ")\n" +
                "📊 Дел: " + user.getTotalEvents() + "\n\n" +
                "1. 📅 Добавить дело\n" +
                "2. 👀 Посмотреть дела\n" +
                "3. 📊 Моя статистика\n" +
                "4. 🔄 Сменить пользователя\n" +
                "5. ♈ Получить гороскоп\n" +
                "6. 🌤️ Погода\n" +
                "7. ℹ️ Помощь\n\n" +
                "Команды:\n" +
                "/name - изменить имя\n" +
                "/switch - сменить пользователя\n" +
                "/stats - статистика\n" +
                "/horoscope - гороскоп на сегодня\n" +
                "/weather - погода\n" +
                "/cancel - отменить текущую операцию\n" +
                "/help - помощь\n\n" +
                "Выберите действие:";
        sendMessage(chatId, menu);
    }

}