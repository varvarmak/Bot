package org.example.services;

import org.example.DataBaseManager;
import org.example.bot.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class UserHistoryCommand {
    private final DataBaseManager databaseManager;
    private final TelegramBot bot;

    public UserHistoryCommand(TelegramBot bot) {
        this.databaseManager = new DataBaseManager();
        this.bot = bot;
    }

    public void execute(Message message) {
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();

        List<UserData> lastReminders = databaseManager.getLastReminders(userId, 5);
        int totalReminders = databaseManager.getUserReminderCount(userId);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(buildHistoryMessage(lastReminders, totalReminders));

        try {
            bot.execute(response);
            System.out.println("История напоминаний успешно отправлена пользователю " + userId);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки истории напоминаний: " + e.getMessage());
        }
    }

    // Добавляем новый метод для вызова по chatId
    public void executeByChatId(Long chatId) {
        try {
            // Используем chatId как userId для поиска в базе данных
            List<UserData> lastReminders = databaseManager.getLastReminders(chatId, 5);
            int totalReminders = databaseManager.getUserReminderCount(chatId);

            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText(buildHistoryMessage(lastReminders, totalReminders));

            bot.execute(response);
            System.out.println("История напоминаний успешно отправлена в чат " + chatId);
        } catch (Exception e) {
            System.err.println("Ошибка отправки истории напоминаний: " + e.getMessage());

            // Отправляем сообщение об ошибке пользователю
            try {
                SendMessage errorMessage = new SendMessage();
                errorMessage.setChatId(chatId.toString());
                errorMessage.setText("❌ Ошибка загрузки истории напоминаний. Попробуйте позже.");
                bot.execute(errorMessage);
            } catch (TelegramApiException ex) {
                System.err.println("Ошибка отправки сообщения об ошибке: " + ex.getMessage());
            }
        }
    }

    public String getCommandName() {
        return "history";
    }

    public String getDescription() {
        return "Показать историю напоминаний";
    }

    private String buildHistoryMessage(List<UserData> reminders, int totalCount) {
        if (reminders.isEmpty()) {
            return "📋 У вас пока нет сохраненных напоминаний.\n" +
                    "Создайте первое напоминание с помощью команды /start!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 **История ваших напоминаний**\n\n");
        sb.append("Всего напоминаний: ").append(totalCount).append("\n\n");
        sb.append("Последние 5 напоминаний:\n\n");

        for (int i = 0; i < reminders.size(); i++) {
            UserData reminder = reminders.get(i);
            sb.append("🔔 **").append(i + 1).append(". ").append(reminder.getReminderName()).append("**\n");
            sb.append("   📅 Дата: ").append(reminder.getDay()).append(" ").append(getMonthName(reminder.getMonth())).append(" ").append(reminder.getYear()).append("\n");
            sb.append("   ⏰ Время: ").append(reminder.getReminderTime()).append("\n");

            if (reminder.getDescription() != null && !reminder.getDescription().isEmpty()) {
                sb.append("   📝 Описание: ").append(reminder.getDescription()).append("\n");
            }

            sb.append("\n");
        }

        sb.append("Для создания нового напоминания используйте команду /start");
        return sb.toString();
    }

    private String getMonthName(String month) {
        try {
            int monthNumber = Integer.parseInt(month);
            String[] monthNames = {"", "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
            if (monthNumber >= 1 && monthNumber <= 12) {
                return monthNames[monthNumber];
            }
        } catch (NumberFormatException e) {
            // Если month уже строка, возвращаем как есть
            return month;
        }
        return month;
    }
}