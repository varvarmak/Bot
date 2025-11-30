package org.example.bot.handlers;

import org.example.services.UserManager;
import org.example.services.UserStateManager;
import org.example.services.MessageService;

public class StateHandler {
    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private MessageSender messageSender;

    public StateHandler(UserManager userManager, UserStateManager stateManager,
                        MessageService messageService, MessageSender messageSender) {
        this.userManager = userManager;
        this.stateManager = stateManager;
        this.messageService = messageService;
        this.messageSender = messageSender;
    }

    public void handleState(Long chatId, String state, String message) {
        org.example.models.User user = userManager.getUserByChatId(chatId);

        switch (state) {
            case "MAIN_MENU":
                handleMainMenu(chatId, message, user);
                break;
            case "ADD_EVENT_YEAR":
                handleAddEventYear(chatId, message);
                break;
            case "ADD_EVENT_MONTH":
                handleAddEventMonth(chatId, message);
                break;
            case "ADD_EVENT_DAY":
                handleAddEventDay(chatId, message);
                break;
            case "ADD_EVENT_TIME":
                handleAddEventTime(chatId, message);
                break;
            case "ADD_EVENT_TITLE":
                handleAddEventTitle(chatId, message);
                break;
            case "ADD_EVENT_DESCRIPTION":
                handleAddEventDescription(chatId, message, user);
                break;
            case "VIEW_EVENTS_YEAR":
                handleViewEventsYear(chatId, message);
                break;
            case "VIEW_EVENTS_MONTH":
                handleViewEventsMonth(chatId, message);
                break;
            case "VIEW_EVENTS_DAY":
                handleViewEventsDay(chatId, message, user);
                break;
            case "CHANGE_USER_NAME":
                handleChangeUserName(chatId, message, user);
                break;
            case "SWITCH_USER":
                handleSwitchUser(chatId, message, user);
                break;
            default:
                messageService.sendMainMenu(chatId, user);
        }
    }

    private void handleMainMenu(Long chatId, String message, org.example.models.User user) {
        switch (message) {
            case "1":
                stateManager.setUserState(chatId, "ADD_EVENT_YEAR");
                messageSender.sendSimpleMessage(chatId, "Введите год (2025-2125):");
                break;
            case "2":
                stateManager.setUserState(chatId, "VIEW_EVENTS_YEAR");
                messageSender.sendSimpleMessage(chatId, "Введите год для просмотра событий:");
                break;
            case "3":
                messageService.sendStatistics(chatId, user);
                stateManager.setUserState(chatId, "MAIN_MENU");
                break;
            case "4":
                stateManager.setUserState(chatId, "SWITCH_USER");
                String usersList = userManager.getAvailableUsersList();
                messageSender.sendSimpleMessage(chatId, "Доступные пользователи:\n" + usersList + "\nВведите имя пользователя:");
                break;
            case "5":
                messageService.sendZodiacButtons(chatId);
                stateManager.setUserState(chatId, "MAIN_MENU");
                break;
            case "6":
                sendHelpMessage(chatId);
                stateManager.setUserState(chatId, "MAIN_MENU");
                break;
            default:
                messageService.sendMainMenu(chatId, user);
        }
    }

    private void sendHelpMessage(Long chatId) {
        String helpText = "ℹ️ Помощь по боту:\n\n" +
                "1. 📅 Добавить дело - создайте новое событие с указанием даты и времени\n" +
                "2. 👀 Посмотреть дела - просмотр событий на определенный день\n" +
                "3. 📊 Моя статистика - общее количество дел\n" +
                "4. 🔄 Сменить пользователя - переключиться на другого пользователя\n" +
                "5. ♈ Получить гороскоп - гороскоп на сегодня\n" +
                "6. ℹ️ Помощь - это сообщение\n\n" +
                "Команды:\n" +
                "/start - начать работу\n" +
                "/name - изменить имя\n" +
                "/stats - статистика\n" +
                "/horoscope - гороскоп\n" +
                "/cancel - отмена операции";

        messageSender.sendSimpleMessage(chatId, helpText);
    }

    private void handleAddEventYear(Long chatId, String message) {
        try {
            int year = Integer.parseInt(message);
            if (year < 2025 || year > 2125) {
                messageSender.sendSimpleMessage(chatId, "Год должен быть 2025-2125:");
                return;
            }
            stateManager.getTempEventData(chatId).year = year;
            stateManager.setUserState(chatId, "ADD_EVENT_MONTH");
            messageService.sendMonthButtons(chatId);
        } catch (NumberFormatException e) {
            messageSender.sendSimpleMessage(chatId, "Введите корректный год:");
        }
    }

    private void handleAddEventMonth(Long chatId, String message) {
        try {
            int month = Integer.parseInt(message);
            if (month < 1 || month > 12) {
                messageSender.sendSimpleMessage(chatId, "Месяц должен быть 1-12:");
                messageService.sendMonthButtons(chatId);
                return;
            }
            stateManager.getTempEventData(chatId).month = month;
            stateManager.setUserState(chatId, "ADD_EVENT_DAY");
            messageService.sendDayButtons(chatId, stateManager.getTempEventData(chatId).year, month);
        } catch (NumberFormatException e) {
            messageSender.sendSimpleMessage(chatId, "Введите корректный месяц:");
            messageService.sendMonthButtons(chatId);
        }
    }

    private void handleAddEventDay(Long chatId, String message) {
        try {
            int day = Integer.parseInt(message);
            stateManager.getTempEventData(chatId).day = day;
            stateManager.setUserState(chatId, "ADD_EVENT_TIME");
            messageSender.sendSimpleMessage(chatId, "Введите время (формат HH:MM):");
        } catch (NumberFormatException e) {
            messageSender.sendSimpleMessage(chatId, "Введите корректный день:");
        }
    }

    private void handleAddEventTime(Long chatId, String message) {
        if (isValidTime(message)) {
            stateManager.getTempEventData(chatId).time = message;
            stateManager.setUserState(chatId, "ADD_EVENT_TITLE");
            messageSender.sendSimpleMessage(chatId, "Введите название события:");
        } else {
            messageSender.sendSimpleMessage(chatId, "❌ Неверный формат времени. Используйте HH:MM (например, 14:30):");
        }
    }

    private void handleAddEventTitle(Long chatId, String message) {
        stateManager.getTempEventData(chatId).title = message;
        stateManager.setUserState(chatId, "ADD_EVENT_DESCRIPTION");
        messageSender.sendSimpleMessage(chatId, "Введите описание события:");
    }

    private void handleAddEventDescription(Long chatId, String message, org.example.models.User user) {
        org.example.models.EventData data = stateManager.getTempEventData(chatId);
        try {
            user.getYear(data.year).addEvent(data.month, data.day, data.time, data.title, message);
            messageSender.sendSimpleMessage(chatId, "✅ Дело добавлено!");
            stateManager.clearTempEventData(chatId);
            stateManager.setUserState(chatId, "MAIN_MENU");
            messageService.sendMainMenu(chatId, user);
        } catch (Exception e) {
            messageSender.sendSimpleMessage(chatId, "❌ Ошибка: " + e.getMessage());
            stateManager.setUserState(chatId, "MAIN_MENU");
            messageService.sendMainMenu(chatId, user);
        }
    }

    private void handleViewEventsYear(Long chatId, String message) {
        try {
            stateManager.getTempEventData(chatId).year = Integer.parseInt(message);
            stateManager.setUserState(chatId, "VIEW_EVENTS_MONTH");
            messageSender.sendSimpleMessage(chatId, "Введите месяц (1-12):");
        } catch (NumberFormatException e) {
            messageSender.sendSimpleMessage(chatId, "Введите корректный год:");
        }
    }

    private void handleViewEventsMonth(Long chatId, String message) {
        try {
            stateManager.getTempEventData(chatId).month = Integer.parseInt(message);
            stateManager.setUserState(chatId, "VIEW_EVENTS_DAY");
            messageSender.sendSimpleMessage(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            messageSender.sendSimpleMessage(chatId, "Введите корректный месяц:");
        }
    }

    private void handleViewEventsDay(Long chatId, String message, org.example.models.User user) {
        try {
            int day = Integer.parseInt(message);
            org.example.models.EventData data = stateManager.getTempEventData(chatId);
            org.example.models.Day dayObj = user.getYear(data.year).getMonth(data.month).getDay(day);

            if (dayObj.getEventsCount() == 0) {
                messageSender.sendSimpleMessage(chatId, "📭 На этот день дел нет");
            } else {
                StringBuilder sb = new StringBuilder("📅 Дела на " + day + "." + data.month + "." + data.year + ":\n\n");
                for (org.example.models.Event event : dayObj.getEvents()) {
                    if (event != null) {
                        sb.append("⏰ ").append(event.getTime()).append(" - ").append(event.getTitle())
                                .append("\n📋 ").append(event.getComm()).append("\n━━━━━━━━━━━━━━━━━━━━\n");
                    }
                }
                messageSender.sendSimpleMessage(chatId, sb.toString());
            }
        } catch (Exception e) {
            messageSender.sendSimpleMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }

        stateManager.clearTempEventData(chatId);
        stateManager.setUserState(chatId, "MAIN_MENU");
        messageService.sendMainMenu(chatId, user);
    }

    private void handleChangeUserName(Long chatId, String newName, org.example.models.User user) {
        if (newName.trim().isEmpty()) {
            messageSender.sendSimpleMessage(chatId, "❌ Имя не может быть пустым");
        } else {
            String oldName = user.getName();
            userManager.updateUserName(user, oldName, newName);
            user.setName(newName);
            messageSender.sendSimpleMessage(chatId, "✅ Имя изменено на: " + newName);
        }
        stateManager.setUserState(chatId, "MAIN_MENU");
        messageService.sendMainMenu(chatId, user);
    }

    private void handleSwitchUser(Long chatId, String userName, org.example.models.User currentUser) {
        org.example.models.User targetUser = userManager.getUserByName(userName);

        if (targetUser == null) {
            messageSender.sendSimpleMessage(chatId, "❌ Пользователь '" + userName + "' не найден");
            messageService.sendMainMenu(chatId, currentUser);
            return;
        }

        if (targetUser.equals(currentUser)) {
            messageSender.sendSimpleMessage(chatId, "❌ Вы уже используете этого пользователя");
            messageService.sendMainMenu(chatId, currentUser);
            return;
        }

        userManager.switchUser(chatId, targetUser);
        stateManager.setUserState(chatId, "MAIN_MENU");
        stateManager.clearTempEventData(chatId);

        messageSender.sendSimpleMessage(chatId, "✅ Переключен на пользователя: " + targetUser.getName());
        messageService.sendMainMenu(chatId, targetUser);
    }

    private boolean isValidTime(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
}