package org.example.bot.handlers;

import org.example.services.UserManager;
import org.example.services.UserStateManager;
import org.example.services.MessageService;
import org.example.utils.UserNameExtractor;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

public class CommandHandler {
    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private MessageSender messageSender;
    private StateHandler stateHandler;

    public CommandHandler(UserManager userManager, UserStateManager stateManager,
                          MessageService messageService, MessageSender messageSender) {
        this.userManager = userManager;
        this.stateManager = stateManager;
        this.messageService = messageService;
        this.messageSender = messageSender;
        this.stateHandler = new StateHandler(userManager, stateManager, messageService, messageSender);
    }

    public void handleStartCommand(Long chatId, User telegramUser) {
        String userName = UserNameExtractor.extractUserName(telegramUser);
        org.example.models.User newUser = userManager.createUser(chatId, userName);
        stateManager.initializeUser(chatId);

        messageSender.sendSimpleMessage(chatId, "👋 Добро пожаловать, " + userName + "!\n" +
                "Я буду звать вас: " + userName + "\n\n" +
                "Если хотите сменить имя, напишите /name");
        messageService.sendMainMenu(chatId, newUser);
    }

    public void handleCommand(Long chatId, String command) {
        org.example.models.User user = userManager.getUserByChatId(chatId);

        switch (command) {
            case "/start":
                stateManager.setUserState(chatId, "MAIN_MENU");
                stateManager.clearTempEventData(chatId);
                messageService.sendMainMenu(chatId, user);
                break;
            case "/name":
                stateManager.setUserState(chatId, "CHANGE_USER_NAME");
                messageSender.sendSimpleMessage(chatId, "✏️ Введите новое имя:");
                break;
            case "/switch":
                stateManager.setUserState(chatId, "SWITCH_USER");
                String usersList = userManager.getAvailableUsersList();
                messageSender.sendSimpleMessage(chatId, "Доступные пользователи:\n" + usersList + "\nВведите имя пользователя:");
                break;
            case "/stats":
                messageService.sendStatistics(chatId, user);
                break;
            case "/horoscope":
                messageService.sendZodiacButtons(chatId);
                break;
            case "/weather":
                messageService.sendWeatherButtons(chatId);
                break;
            case "/cancel":
                stateManager.setUserState(chatId, "MAIN_MENU");
                stateManager.clearTempEventData(chatId);
                messageSender.sendSimpleMessage(chatId, "❌ Операция отменена");
                messageService.sendMainMenu(chatId, user);
                break;
            case "/debug_reminders":
                messageSender.sendSimpleMessage(chatId, "🔧 Режим отладки напоминаний активирован");
                testReminderSystem(chatId, user);
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            default:
                messageSender.sendSimpleMessage(chatId, "❌ Неизвестная команда. Используйте /help");
        }
    }

    public void handleState(Long chatId, String state, String message) {
        stateHandler.handleState(chatId, state, message);
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "ℹ️ Помощь по командам:\n\n" +
                "/start - начать работу\n" +
                "/name - изменить имя\n" +
                "/switch - сменить пользователя\n" +
                "/stats - статистика\n" +
                "/horoscope - гороскоп на сегодня\n" +
                "/cancel - отменить текущую операцию\n" +
                "/help - помощь\n\n" +
                "Или используйте меню ниже:";

        messageSender.sendSimpleMessage(chatId, helpText);
        messageService.sendMainMenu(chatId, userManager.getUserByChatId(chatId));
    }

    private void testReminderSystem(Long chatId, org.example.models.User user) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime testTime = now.plusMinutes(2);

            String time = String.format("%02d:%02d", testTime.getHour(), testTime.getMinute());
            int year = testTime.getYear();
            int month = testTime.getMonthValue();
            int day = testTime.getDayOfMonth();

            user.getYear(year).addEvent(month, day, time, "ТЕСТОВОЕ НАПОМИНАНИЕ", "Это тест напоминания");

            String debugInfo = "🧪 ТЕСТ НАПОМИНАНИЙ\n" +
                    "Создано тестовое событие:\n" +
                    "📅 " + day + "." + month + "." + year + "\n" +
                    "⏰ " + time + "\n" +
                    "📝 ТЕСТОВОЕ НАПОМИНАНИЕ\n\n" +
                    "Напоминание должно прийти через 1 минуту";

            messageSender.sendSimpleMessage(chatId, debugInfo);

        } catch (Exception e) {
            messageSender.sendSimpleMessage(chatId, "❌ Ошибка теста: " + e.getMessage());
        }
    }
}