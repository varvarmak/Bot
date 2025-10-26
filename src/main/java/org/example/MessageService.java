package org.example;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private TelegramBot bot;

    public MessageService(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    public void sendMonthButtons(Long chatId) {
        String[] months = {
                "Январь","Февраль","Март","Апрель","Май","Июнь",
                "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"
        };

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (int i = 0; i < months.length; i += 3) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = i; j < i + 3 && j < months.length; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(months[j]);
                button.setCallbackData(months[j]);
                row.add(button);
            }
            rows.add(row);
        }

        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите месяц:");
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDayButtons(Long chatId, int year, int month) {
        int daysInMonth = new Month(month).getDaysInMonth();

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
        message.setChatId(chatId);
        message.setText("Выберите день:");
        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
                "5. ℹ️ Помощь\n\n" +
                "Команды:\n" +
                "/name - изменить имя\n" +
                "/switch - сменить пользователя\n" +
                "/stats - статистика\n" +
                "/help - помощь\n\n" +
                "Выберите действие:";
        sendMessage(chatId, menu);
    }

    public void sendHelpMessage(Long chatId) {
        String helpText = "🤖 Помощь по использованию бота:\n\n" +
                "📋 Основные команды:\n" +
                "1 - Добавить дело\n" +
                "2 - Посмотреть дела\n" +
                "3 - Моя статистика\n" +
                "4 - Сменить пользователя\n\n" +
                "⚡ Быстрые команды:\n" +
                "/name - изменить своё имя\n" +
                "/switch - переключиться на другого пользователя\n" +
                "/stats - показать статистику\n" +
                "/help - эта справка\n\n" +
                "📅 Форматы:\n" +
                "Время: HH:MM (например, 14:30)\n" +
                "Год: 2025-2125\n" +
                "Месяц: 1-12\n\n" +
                "👥 Для переключения пользователя нужно знать его имя!";
        sendMessage(chatId, helpText);
    }

    public void sendStatistics(Long chatId, User user) {
        String stats = "📊 Статистика пользователя " + user.getName() + ":\n" +
                "👤 ID: " + user.getId() + "\n" +
                "📈 Всего дел: " + user.getTotalEvents();
        sendMessage(chatId, stats);
    }
}
