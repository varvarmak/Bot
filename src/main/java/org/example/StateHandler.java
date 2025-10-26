package org.example;

public class StateHandler {
    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;

    public StateHandler(UserManager userManager, UserStateManager stateManager, MessageService messageService) {
        this.userManager = userManager;
        this.stateManager = stateManager;
        this.messageService = messageService;
    }

    public void handleState(Long chatId, String state, String message) {
        User user = userManager.getUserByChatId(chatId);

        switch (state) {
            case "MAIN_MENU": handleMainMenu(chatId, message, user); break;
            case "ADD_EVENT_YEAR": handleAddEventYear(chatId, message); break;
            case "ADD_EVENT_MONTH": handleAddEventMonth(chatId, message); break;
            case "ADD_EVENT_DAY": handleAddEventDay(chatId, message); break;
            case "ADD_EVENT_TIME": handleAddEventTime(chatId, message); break;
            case "ADD_EVENT_TITLE": handleAddEventTitle(chatId, message); break;
            case "ADD_EVENT_DESCRIPTION": handleAddEventDescription(chatId, message, user); break;
            case "VIEW_EVENTS_YEAR": handleViewEventsYear(chatId, message); break;
            case "VIEW_EVENTS_MONTH": handleViewEventsMonth(chatId, message); break;
            case "VIEW_EVENTS_DAY": handleViewEventsDay(chatId, message, user); break;
            case "CHANGE_USER_NAME": handleChangeUserName(chatId, message, user); break;
            case "SWITCH_USER": handleSwitchUser(chatId, message, user); break;
        }
    }

    private void handleMainMenu(Long chatId, String message, User user) {
        switch (message) {
            case "1": stateManager.setUserState(chatId, "ADD_EVENT_YEAR"); messageService.sendMessage(chatId, "Введите год (2025-2125):"); break;
            case "2": stateManager.setUserState(chatId, "VIEW_EVENTS_YEAR"); messageService.sendMessage(chatId, "Введите год:"); break;
            case "3": messageService.sendStatistics(chatId, user); break;
            case "4": stateManager.setUserState(chatId, "SWITCH_USER"); messageService.sendMessage(chatId, "Введите имя пользователя:"); break;
            case "5": messageService.sendHelpMessage(chatId); break;
            default: messageService.sendMainMenu(chatId, user);
        }
    }

    private void handleAddEventYear(Long chatId, String message) {
        try {
            int year = Integer.parseInt(message);
            if (year < 2025 || year > 2125) {
                messageService.sendMessage(chatId, "Год должен быть 2025-2125:"); return;
            }
            stateManager.getTempEventData(chatId).year = year;
            stateManager.setUserState(chatId, "ADD_EVENT_MONTH");
            messageService.sendMessage(chatId, "Введите месяц (1-12):");
        } catch (NumberFormatException e) {
            messageService.sendMessage(chatId, "Введите корректный год:");
        }
    }

    private void handleAddEventMonth(Long chatId, String message) {
        try {
            int month = Integer.parseInt(message);
            if (month < 1 || month > 12) {
                messageService.sendMessage(chatId, "Месяц должен быть 1-12:"); return;
            }
            stateManager.getTempEventData(chatId).month = month;
            stateManager.setUserState(chatId, "ADD_EVENT_DAY");
            messageService.sendMessage(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            messageService.sendMessage(chatId, "Введите корректный месяц:");
        }
    }

    private void handleAddEventDay(Long chatId, String message) {
        try {
            int day = Integer.parseInt(message);
            stateManager.getTempEventData(chatId).day = day;
            stateManager.setUserState(chatId, "ADD_EVENT_TIME");
            messageService.sendMessage(chatId, "Введите время:");
        } catch (NumberFormatException e) {
            messageService.sendMessage(chatId, "Введите корректный день:");
        }
    }

    private void handleAddEventTime(Long chatId, String message) {
        stateManager.getTempEventData(chatId).time = message;
        stateManager.setUserState(chatId, "ADD_EVENT_TITLE");
        messageService.sendMessage(chatId, "Введите название:");
    }

    private void handleAddEventTitle(Long chatId, String message) {
        stateManager.getTempEventData(chatId).title = message;
        stateManager.setUserState(chatId, "ADD_EVENT_DESCRIPTION");
        messageService.sendMessage(chatId, "Введите описание:");
    }

    private void handleAddEventDescription(Long chatId, String message, User user) {
        EventData data = stateManager.getTempEventData(chatId);
        try {
            user.getYear(data.year).addEvent(data.month, data.day, data.time, data.title, message);
            messageService.sendMessage(chatId, "✅ Дело добавлено!");
        } catch (Exception e) {
            messageService.sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }
        messageService.sendMainMenu(chatId, user);
    }

    private void handleViewEventsYear(Long chatId, String message) {
        try {
            stateManager.getTempEventData(chatId).year = Integer.parseInt(message);
            stateManager.setUserState(chatId, "VIEW_EVENTS_MONTH");
            messageService.sendMessage(chatId, "Введите месяц:");
        } catch (NumberFormatException e) {
            messageService.sendMessage(chatId, "Введите корректный год:");
        }
    }

    private void handleViewEventsMonth(Long chatId, String message) {
        try {
            stateManager.getTempEventData(chatId).month = Integer.parseInt(message);
            stateManager.setUserState(chatId, "VIEW_EVENTS_DAY");
            messageService.sendMessage(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            messageService.sendMessage(chatId, "Введите корректный месяц:");
        }
    }

    private void handleViewEventsDay(Long chatId, String message, User user) {
        try {
            int day = Integer.parseInt(message);
            EventData data = stateManager.getTempEventData(chatId);
            Day dayObj = user.getYear(data.year).getMonth(data.month).getDay(day);

            if (dayObj.getEventsCount() == 0) {
                messageService.sendMessage(chatId, "📭 Дел нет");
            } else {
                StringBuilder sb = new StringBuilder("📅 Дела:\n\n");
                for (Event event : dayObj.getEvents()) {
                    if (event != null) {
                        sb.append("⏰ ").append(event.getTime()).append(" - ").append(event.getTitle()).append("\n📋 ").append(event.getComm()).append("\n━━━━━━━━━━━━━━━━━━━━\n");
                    }
                }
                messageService.sendMessage(chatId, sb.toString());
            }
        } catch (Exception e) {
            messageService.sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }
        messageService.sendMainMenu(chatId, user);
    }

    private void handleChangeUserName(Long chatId, String newName, User user) {
        if (newName.trim().isEmpty()) {
            messageService.sendMessage(chatId, "❌ Имя не может быть пустым");
        } else {
            userManager.updateUserName(user, user.getName(), newName);
            user.setName(newName);
            messageService.sendMessage(chatId, "✅ Имя изменено");
        }
        messageService.sendMainMenu(chatId, user);
    }

    private void handleSwitchUser(Long chatId, String userName, User currentUser) {
        User targetUser = userManager.getUserByName(userName);

        if (targetUser == null) {
            messageService.sendMessage(chatId, "❌ Пользователь '" + userName + "' не найден");
            messageService.sendMainMenu(chatId, currentUser);
            return;
        }

        if (targetUser.equals(currentUser)) {
            messageService.sendMessage(chatId, "❌ Вы уже используете этого пользователя");
            messageService.sendMainMenu(chatId, currentUser);
            return;
        }


        userManager.switchUser(chatId, targetUser);


        stateManager.setUserState(chatId, "MAIN_MENU");

        messageService.sendMessage(chatId, "✅ Переключен на пользователя: " + targetUser.getName());
        messageService.sendMainMenu(chatId, targetUser);
    }
}
