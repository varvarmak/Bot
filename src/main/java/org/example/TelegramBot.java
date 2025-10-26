package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TelegramBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;

    // Хранилище данных
    private Map<Long, User> users = new HashMap<>();
    private Map<Long, String> userStates = new HashMap<>();
    private Map<Long, EventData> tempEventData = new HashMap<>();
    private Map<String, User> usersByName = new HashMap<>();
    private int nextUserId = 1;
    private int reminderMinutes = 1;

    public TelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        startReminderThread();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        String userName = extractUserName(update.getMessage().getFrom());


        if (!users.containsKey(chatId)) {
            User newUser = new User(nextUserId++, userName);
            users.put(chatId, newUser);
            usersByName.put(userName.toLowerCase(), newUser);
            userStates.put(chatId, "MAIN_MENU");
            tempEventData.put(chatId, new EventData());

            sendMsg(chatId, "👋 Добро пожаловать, " + userName + "!\n" +
                    "Я буду звать вас: " + userName + "\n\n" +
                    "Если хотите сменить имя, напишите /name");
            showMainMenu(chatId);
            return;
        }

        if (text.startsWith("/")) {
            handleCommand(chatId, text);
            return;
        }

        String state = userStates.get(chatId);
        User currentUser = users.get(chatId);

        switch (state) {
            case "MAIN_MENU":
                handleMainMenu(chatId, text, currentUser);
                break;
            case "ADD_EVENT_YEAR":
                handleAddEventYear(chatId, text);
                break;
            case "ADD_EVENT_MONTH":
                handleAddEventMonth(chatId, text);
                break;
            case "ADD_EVENT_DAY":
                handleAddEventDay(chatId, text);
                break;
            case "ADD_EVENT_TIME":
                handleAddEventTime(chatId, text);
                break;
            case "ADD_EVENT_TITLE":
                handleAddEventTitle(chatId, text);
                break;
            case "ADD_EVENT_DESCRIPTION":
                handleAddEventDescription(chatId, text, currentUser);
                break;
            case "VIEW_EVENTS_YEAR":
                handleViewEventsYear(chatId, text);
                break;
            case "VIEW_EVENTS_MONTH":
                handleViewEventsMonth(chatId, text);
                break;
            case "VIEW_EVENTS_DAY":
                handleViewEventsDay(chatId, text, currentUser);
                break;
            case "CHANGE_USER_NAME":
                handleChangeUserName(chatId, text);
                break;
            case "SWITCH_USER":
                handleSwitchUser(chatId, text);
                break;
        }
    }

    private String extractUserName(org.telegram.telegrambots.meta.api.objects.User from) {
        String firstName = from.getFirstName() != null ? from.getFirstName() : "";
        String lastName = from.getLastName() != null ? from.getLastName() : "";

        if (!firstName.isEmpty()) {
            return firstName + (lastName.isEmpty() ? "" : " " + lastName);
        }

        String userName = from.getUserName();
        return userName != null ? "@" + userName : "User" + from.getId();
    }

    private void handleCommand(long chatId, String command) {
        switch (command) {
            case "/start":
                showMainMenu(chatId);
                break;
            case "/name":
                userStates.put(chatId, "CHANGE_USER_NAME");
                sendMsg(chatId, "✏️ Введите новое имя:");
                break;
            case "/switch":
                userStates.put(chatId, "SWITCH_USER");
                sendMsg(chatId, "🔄 Введите имя пользователя для переключения:");
                break;
            case "/stats":
                showStatistics(chatId, users.get(chatId));
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            default:
                sendMsg(chatId, "❌ Неизвестная команда. Используйте /help");
        }
    }

    private void handleMainMenu(Long chatId, String message, User user) {
        switch (message) {
            case "1":
                userStates.put(chatId, "ADD_EVENT_YEAR");
                sendMsg(chatId, "Введите год (2025-2125):");
                break;
            case "2":
                userStates.put(chatId, "VIEW_EVENTS_YEAR");
                sendMsg(chatId, "Введите год:");
                break;
            case "3":
                showStatistics(chatId, user);
                break;
            case "4":
                userStates.put(chatId, "SWITCH_USER");
                sendMsg(chatId, "🔄 Введите имя пользователя для переключения:\n\n" +
                        "Доступные пользователи:\n" + getAvailableUsers());
                break;
            case "5":
                sendHelpMessage(chatId);
                break;
            default:
                showMainMenu(chatId);
        }
    }

    private void showMainMenu(Long chatId) {
        User currentUser = users.get(chatId);
        String menu = "🎯 Главное меню\n" +
                "👤 Текущий пользователь: " + currentUser.getName() +
                " (ID: " + currentUser.getId() + ")\n" +
                "📊 Дел: " + currentUser.getTotalEvents() + "\n\n" +
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
        sendMsg(chatId, menu);
        userStates.put(chatId, "MAIN_MENU");
    }

    private void sendHelpMessage(Long chatId) {
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
        sendMsg(chatId, helpText);
        showMainMenu(chatId);
    }

    private void handleChangeUserName(Long chatId, String newName) {
        if (newName.trim().isEmpty()) {
            sendMsg(chatId, "❌ Имя не может быть пустым");
            userStates.put(chatId, "MAIN_MENU");
            showMainMenu(chatId);
            return;
        }

        User currentUser = users.get(chatId);
        String oldName = currentUser.getName();
        usersByName.remove(oldName.toLowerCase());

        currentUser.setName(newName);
        usersByName.put(newName.toLowerCase(), currentUser);

        sendMsg(chatId, "✅ Имя изменено на: " + newName);
        showMainMenu(chatId);
    }

    private void handleSwitchUser(Long chatId, String userName) {
        User targetUser = usersByName.get(userName.toLowerCase());

        if (targetUser == null) {
            sendMsg(chatId, "❌ Пользователь '" + userName + "' не найден\n\n" +
                    "Доступные пользователи:\n" + getAvailableUsers());
            userStates.put(chatId, "MAIN_MENU");
            showMainMenu(chatId);
            return;
        }

        users.put(chatId, targetUser);
        tempEventData.put(chatId, new EventData());

        sendMsg(chatId, "✅ Переключен на пользователя: " + targetUser.getName() +
                "\nID: " + targetUser.getId() +
                "\nДел: " + targetUser.getTotalEvents());
        showMainMenu(chatId);
    }

    private String getAvailableUsers() {
        StringBuilder sb = new StringBuilder();
        for (User user : usersByName.values()) {
            sb.append("• ").append(user.getName())
                    .append(" (ID: ").append(user.getId())
                    .append(", дел: ").append(user.getTotalEvents())
                    .append(")\n");
        }
        return sb.toString().isEmpty() ? "Нет зарегистрированных пользователей" : sb.toString();
    }

    private void handleAddEventYear(Long chatId, String message) {
        try {
            int year = Integer.parseInt(message);
            if (year < 2025 || year > 2125) {
                sendMsg(chatId, "Год должен быть в диапазоне 2025-2125. Попробуйте снова:");
                return;
            }
            tempEventData.get(chatId).year = year;
            userStates.put(chatId, "ADD_EVENT_MONTH");
            sendMsg(chatId, "Введите месяц (1-12):");
        } catch (NumberFormatException e) {
            sendMsg(chatId, "Пожалуйста, введите корректный год:");
        }
    }

    private void handleAddEventMonth(Long chatId, String message) {
        try {
            int month = Integer.parseInt(message);
            if (month < 1 || month > 12) {
                sendMsg(chatId, "Месяц должен быть в диапазоне 1-12. Попробуйте снова:");
                return;
            }
            tempEventData.get(chatId).month = month;
            userStates.put(chatId, "ADD_EVENT_DAY");
            sendMsg(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            sendMsg(chatId, "Пожалуйста, введите корректный месяц:");
        }
    }

    private void handleAddEventDay(Long chatId, String message) {
        try {
            int day = Integer.parseInt(message);
            tempEventData.get(chatId).day = day;
            userStates.put(chatId, "ADD_EVENT_TIME");
            sendMsg(chatId, "Введите время (например, 14:30):");
        } catch (NumberFormatException e) {
            sendMsg(chatId, "Пожалуйста, введите корректный день:");
        }
    }

    private void handleAddEventTime(Long chatId, String message) {
        tempEventData.get(chatId).time = message;
        userStates.put(chatId, "ADD_EVENT_TITLE");
        sendMsg(chatId, "Введите название дела:");
    }

    private void handleAddEventTitle(Long chatId, String message) {
        tempEventData.get(chatId).title = message;
        userStates.put(chatId, "ADD_EVENT_DESCRIPTION");
        sendMsg(chatId, "Введите описание:");
    }

    private void handleAddEventDescription(Long chatId, String message, User user) {
        EventData data = tempEventData.get(chatId);

        try {
            Year yearObj = user.getYear(data.year);
            yearObj.addEvent(data.month, data.day, data.time, data.title, message);

            sendMsg(chatId, "✅ Дело добавлено!\n" +
                    "📅 " + data.day + "." + data.month + "." + data.year +
                    " ⏰ " + data.time + "\n" +
                    "📝 " + data.title);
        } catch (Exception e) {
            sendMsg(chatId, "❌ Ошибка: " + e.getMessage());
        }

        showMainMenu(chatId);
    }

    private void handleViewEventsYear(Long chatId, String message) {
        try {
            int year = Integer.parseInt(message);
            tempEventData.get(chatId).year = year;
            userStates.put(chatId, "VIEW_EVENTS_MONTH");
            sendMsg(chatId, "Введите месяц (1-12):");
        } catch (NumberFormatException e) {
            sendMsg(chatId, "Пожалуйста, введите корректный год:");
        }
    }

    private void handleViewEventsMonth(Long chatId, String message) {
        try {
            int month = Integer.parseInt(message);
            tempEventData.get(chatId).month = month;
            userStates.put(chatId, "VIEW_EVENTS_DAY");
            sendMsg(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            sendMsg(chatId, "Пожалуйста, введите корректный месяц:");
        }
    }

    private void handleViewEventsDay(Long chatId, String message, User user) {
        try {
            int day = Integer.parseInt(message);
            EventData data = tempEventData.get(chatId);

            Year yearObj = user.getYear(data.year);
            Month monthObj = yearObj.getMonth(data.month);
            Day dayObj = monthObj.getDay(day);

            int eventsCount = dayObj.getEventsCount();

            if (eventsCount == 0) {
                sendMsg(chatId, "📭 На " + day + "." + data.month + "." + data.year + " дел нет");
            } else {
                StringBuilder eventsText = new StringBuilder();
                eventsText.append("📅 Дела на ").append(day).append(".")
                        .append(data.month).append(".").append(data.year).append(":\n\n");

                Event[] events = dayObj.getEvents();
                for (Event event : events) {
                    if (event != null) {
                        eventsText.append("⏰ ").append(event.getTime())
                                .append(" - ").append(event.getTitle())
                                .append("\n📋 ").append(event.getComm())
                                .append("\n━━━━━━━━━━━━━━━━━━━━\n");
                    }
                }
                eventsText.append("\nВсего дел: ").append(eventsCount);
                sendMsg(chatId, eventsText.toString());
            }
        } catch (Exception e) {
            sendMsg(chatId, "❌ Ошибка: " + e.getMessage());
        }

        showMainMenu(chatId);
    }

    private void showStatistics(Long chatId, User user) {
        int totalEvents = user.getTotalEvents();
        sendMsg(chatId,
                "📊 Статистика пользователя " + user.getName() + ":\n" +
                        "👤 ID: " + user.getId() + "\n" +
                        "📈 Всего дел: " + totalEvents);

        showMainMenu(chatId);
    }

    private void startReminderThread() {
        new Thread(() -> {
            while (true) {
                try {
                    checkReminders();
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.out.println("Ошибка в checkReminders: " + e.getMessage());
                }
            }
        }, "ReminderThread").setDaemon(true);
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        for (Map.Entry<Long, User> entry : users.entrySet()) {
            Long chatId = entry.getKey();
            User user = entry.getValue();

            for (int year = currentYear - 1; year <= currentYear + 1; year++) {
                Year yearObj = user.getExistingYear(year);
                if (yearObj == null) continue;

                for (int month = 1; month <= 12; month++) {
                    Month monthObj = yearObj.getExistingMonth(month);
                    if (monthObj == null) continue;

                    int maxDay = monthObj.getDaysInMonth();
                    for (int day = 1; day <= maxDay; day++) {
                        Day dayObj = monthObj.getExistingDay(day);
                        if (dayObj == null) continue;

                        for (Event e : dayObj.getEvents()) {
                            if (e == null || e.isReminded()) continue;

                            try {
                                String[] parts = e.getTime().split(":");
                                int hour = Integer.parseInt(parts[0]);
                                int minute = Integer.parseInt(parts[1]);

                                LocalDateTime eventTime = LocalDateTime.of(year, month, day, hour, minute);
                                long minutesUntil = Duration.between(now, eventTime).toMinutes();

                                if (minutesUntil <= reminderMinutes && minutesUntil >= 0) {
                                    String text = "⏰ Напоминание!\n" +
                                            "Скоро: " + e.getTitle() + "\n" +
                                            "🕒 В " + e.getTime() + "\n" +
                                            "📅 " + day + "." + month + "." + year;
                                    sendMsg(chatId, text);
                                    e.setReminded(true);
                                }
                            } catch (Exception ex) {
                                // Ошибка в формате времени
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}

class EventData {
    public int year;
    public int month;
    public int day;
    public String time;
    public String title;
}
