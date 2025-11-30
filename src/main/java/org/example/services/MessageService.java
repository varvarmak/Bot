package org.example.services;

import org.example.bot.TelegramBot;
import org.example.models.User;
import org.example.utils.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageService {
    private TelegramBot bot;
    private UserHistoryCommand historyCommand;

    public MessageService(TelegramBot bot) {
        this.bot = bot;
        this.historyCommand = new UserHistoryCommand(bot);
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

    // Этот метод теперь должен только отправлять меню, а не обрабатывать состояния
    public void sendMainMenu(Long chatId, User user) {
        String menu = "🎯 Главное меню\n" +
                "👤 Текущий пользователь: " + user.getName() +
                " (ID: " + user.getId() + ")\n" +
                "📊 Дел: " + user.getTotalEvents() + "\n\n" +
                "Выберите действие:";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(menu);
        message.setReplyMarkup(createMainMenuKeyboard());

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки главного меню: " + e.getMessage());
        }
    }

    // Удаляем handleTextMessage из MessageService - он должен быть в StateHandler

    public void executeHistoryCommand(Long chatId) {
        historyCommand.executeByChatId(chatId);
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
        InlineKeyboardMarkup markup = KeyboardFactory.createDayButtons(year, month);

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

    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("📅 Добавить дело");
        row1.add("👀 Посмотреть дела");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("📊 Моя статистика");
        row2.add("📋 История напоминаний");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("🔄 Сменить пользователя");
        row3.add("♈ Гороскоп");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("ℹ️ Помощь");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public void sendHelpMessage(Long chatId, User user) {
        String helpText = "ℹ️ Помощь по боту:\n\n" +
                "📅 Добавить дело - создайте новое событие с указанием даты и времени\n" +
                "👀 Посмотреть дела - просмотр событий на определенный день\n" +
                "📊 Моя статистика - общее количество дел\n" +
                "📋 История напоминаний - просмотр последних сохраненных напоминаний\n" +
                "🔄 Сменить пользователя - переключиться на другого пользователя\n" +
                "♈ Гороскоп - гороскоп на сегодня\n" +
                "ℹ️ Помощь - это сообщение\n\n" +
                "Команды:\n" +
                "/start - начать работу\n" +
                "/name - изменить имя\n" +
                "/stats - статистика\n" +
                "/horoscope - гороскоп\n" +
                "/history - история напоминаний\n" +
                "/cancel - отмена операции";

        sendMessage(chatId, helpText);
    }
}