package org.example;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TelegramBot {

    private Map<Long, User> users = new HashMap<>();
    private Map<Long, String> userStates = new HashMap<>();
    private Map<Long, EventData> tempEventData = new HashMap<>();
    private Map<String, User> usersByName = new HashMap<>(); // Для поиска по имени
    private int nextUserId = 1;
    private String botToken;
    private int lastUpdateId = 0;
    private ObjectMapper objectMapper = new ObjectMapper();

    public TelegramBot(String botToken) {
        this.botToken = botToken;
    }

    public void start() {
        System.out.println("✅ Бот запущен! Токен: " + botToken.substring(0, 10) + "...");

        while (true) {
            try {
                checkMessages();
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void checkMessages() throws IOException {
        String url = "https://api.telegram.org/bot" + botToken + "/getUpdates?offset=" + (lastUpdateId + 1);
        String response = sendGetRequest(url);

        JsonNode root = objectMapper.readTree(response);
        if (root.get("ok").asBoolean()) {
            JsonNode updates = root.get("result");
            for (JsonNode update : updates) {
                processUpdate(update);
            }
        }
    }

    private void processUpdate(JsonNode update) {
        lastUpdateId = update.get("update_id").asInt();

        if (update.has("message")) {
            JsonNode message = update.get("message");
            Long chatId = message.get("chat").get("id").asLong();

            // Получаем информацию о пользователе
            String userName = "Неизвестный";
            String firstName = "";
            String lastName = "";

            if (message.has("from")) {
                JsonNode from = message.get("from");
                if (from.has("first_name")) {
                    firstName = from.get("first_name").asText();
                }
                if (from.has("last_name")) {
                    lastName = from.get("last_name").asText();
                }
                if (from.has("username")) {
                    userName = "@" + from.get("username").asText();
                }
            }

            // Формируем имя пользователя
            if (!firstName.isEmpty()) {
                userName = firstName + (lastName.isEmpty() ? "" : " " + lastName);
            }

            if (message.has("text")) {
                String text = message.get("text").asText();
                handleUserMessage(chatId, text, userName);
            }
        }
    }

    private void sendTelegramMessage(Long chatId, String text) {
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            String postData = "chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "UTF-8");
            sendPostRequest(url, postData);
        } catch (Exception e) {
            System.out.println("❌ Ошибка отправки: " + e.getMessage());
        }
    }

    // Остальные HTTP методы без изменений
    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

    private void sendPostRequest(String urlString, String postData) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = postData.getBytes("UTF-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {}
        }
    }

    // ОБНОВЛЕННЫЕ МЕТОДЫ ДЛЯ РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ
    private void handleUserMessage(Long chatId, String message, String telegramName) {
        if (!users.containsKey(chatId)) {
            // Новый пользователь - просим ввести имя
            User newUser = new User(nextUserId++, telegramName);
            users.put(chatId, newUser);
            usersByName.put(telegramName.toLowerCase(), newUser);
            userStates.put(chatId, "MAIN_MENU");
            tempEventData.put(chatId, new EventData());

            sendTelegramMessage(chatId, "👋 Добро пожаловать, " + telegramName + "!\n" +
                    "Я буду звать вас: " + telegramName + "\n\n" +
                    "Если хотите сменить имя, напишите команду /name");
            showMainMenu(chatId);
            return;
        }

        // Обработка команд
        if (message.startsWith("/")) {
            handleCommand(chatId, message);
            return;
        }

        String state = userStates.get(chatId);
        User currentUser = users.get(chatId);

        switch (state) {
            case "MAIN_MENU":
                handleMainMenu(chatId, message, currentUser);
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
                handleAddEventDescription(chatId, message, currentUser);
                break;
            case "VIEW_EVENTS_YEAR":
                handleViewEventsYear(chatId, message);
                break;
            case "VIEW_EVENTS_MONTH":
                handleViewEventsMonth(chatId, message);
                break;
            case "VIEW_EVENTS_DAY":
                handleViewEventsDay(chatId, message, currentUser);
                break;
            case "CHANGE_USER_NAME":
                handleChangeUserName(chatId, message);
                break;
            case "SWITCH_USER":
                handleSwitchUser(chatId, message);
                break;
        }
    }

    // Обработка команд
    private void handleCommand(Long chatId, String command) {
        switch (command) {
            case "/start":
                showMainMenu(chatId);
                break;
            case "/name":
                userStates.put(chatId, "CHANGE_USER_NAME");
                sendTelegramMessage(chatId, "✏️ Введите новое имя:");
                break;
            case "/switch":
                userStates.put(chatId, "SWITCH_USER");
                sendTelegramMessage(chatId, "🔄 Введите имя пользователя для переключения:");
                break;
            case "/stats":
                showStatistics(chatId, users.get(chatId));
                break;
            case "/help":
                sendHelpMessage(chatId);
                break;
            default:
                sendTelegramMessage(chatId, "❌ Неизвестная команда. Используйте /help для списка команд");
                break;
        }
    }

    // Смена имени пользователя
    private void handleChangeUserName(Long chatId, String newName) {
        if (newName.trim().isEmpty()) {
            sendTelegramMessage(chatId, "❌ Имя не может быть пустым");
            userStates.put(chatId, "MAIN_MENU");
            showMainMenu(chatId);
            return;
        }

        User currentUser = users.get(chatId);
        String oldName = currentUser.getName();

        // Удаляем старое имя из поиска
        usersByName.remove(oldName.toLowerCase());

        // Обновляем имя
        currentUser.setName(newName);
        usersByName.put(newName.toLowerCase(), currentUser);

        userStates.put(chatId, "MAIN_MENU");
        sendTelegramMessage(chatId, "✅ Имя изменено на: " + newName);
        showMainMenu(chatId);
    }

    // Переключение между пользователями
    private void handleSwitchUser(Long chatId, String userName) {
        User targetUser = usersByName.get(userName.toLowerCase());

        if (targetUser == null) {
            sendTelegramMessage(chatId, "❌ Пользователь '" + userName + "' не найден\n\n" +
                    "Доступные пользователи:\n" + getAvailableUsers());
            userStates.put(chatId, "MAIN_MENU");
            showMainMenu(chatId);
            return;
        }

        // Переключаем пользователя для этого chatId
        users.put(chatId, targetUser);
        userStates.put(chatId, "MAIN_MENU");
        tempEventData.put(chatId, new EventData());

        sendTelegramMessage(chatId, "✅ Переключен на пользователя: " + targetUser.getName() +
                "\nID: " + targetUser.getId() +
                "\nДел: " + targetUser.getTotalEvents());
        showMainMenu(chatId);
    }

    // Получение списка доступных пользователей
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

    // ОБНОВЛЕННОЕ ГЛАВНОЕ МЕНЮ
    private void handleMainMenu(Long chatId, String message, User user) {
        switch (message) {
            case "1":
                userStates.put(chatId, "ADD_EVENT_YEAR");
                sendTelegramMessage(chatId, "Введите год (2025-2125):");
                break;
            case "2":
                userStates.put(chatId, "VIEW_EVENTS_YEAR");
                sendTelegramMessage(chatId, "Введите год:");
                break;
            case "3":
                showStatistics(chatId, user);
                break;
            case "4":
                userStates.put(chatId, "SWITCH_USER");
                sendTelegramMessage(chatId, "🔄 Введите имя пользователя для переключения:\n\n" +
                        "Доступные пользователи:\n" + getAvailableUsers());
                break;
            case "5":
                sendHelpMessage(chatId);
                break;
            default:
                showMainMenu(chatId);
                break;
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
        sendTelegramMessage(chatId, menu);
    }

    // ОБНОВЛЕННАЯ ПОМОЩЬ
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
        sendTelegramMessage(chatId, helpText);
        showMainMenu(chatId);
    }

    // Остальные методы без изменений (handleAddEventYear, handleAddEventMonth и т.д.)
    private void handleAddEventYear(Long chatId, String message) {
        try {
            int year = Integer.parseInt(message);
            if (year < 2025 || year > 2125) {
                sendTelegramMessage(chatId, "Год должен быть в диапазоне 2025-2125. Попробуйте снова:");
                return;
            }
            tempEventData.get(chatId).year = year;
            userStates.put(chatId, "ADD_EVENT_MONTH");
            sendTelegramMessage(chatId, "Введите месяц (1-12):");
        } catch (NumberFormatException e) {
            sendTelegramMessage(chatId, "Пожалуйста, введите корректный год:");
        }
    }

    private void handleAddEventMonth(Long chatId, String message) {
        try {
            int month = Integer.parseInt(message);
            if (month < 1 || month > 12) {
                sendTelegramMessage(chatId, "Месяц должен быть в диапазоне 1-12. Попробуйте снова:");
                return;
            }
            tempEventData.get(chatId).month = month;
            userStates.put(chatId, "ADD_EVENT_DAY");
            sendTelegramMessage(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            sendTelegramMessage(chatId, "Пожалуйста, введите корректный месяц:");
        }
    }

    private void handleAddEventDay(Long chatId, String message) {
        try {
            int day = Integer.parseInt(message);
            tempEventData.get(chatId).day = day;
            userStates.put(chatId, "ADD_EVENT_TIME");
            sendTelegramMessage(chatId, "Введите время (например, 14:30):");
        } catch (NumberFormatException e) {
            sendTelegramMessage(chatId, "Пожалуйста, введите корректный день:");
        }
    }

    private void handleAddEventTime(Long chatId, String message) {
        tempEventData.get(chatId).time = message;
        userStates.put(chatId, "ADD_EVENT_TITLE");
        sendTelegramMessage(chatId, "Введите название дела:");
    }

    private void handleAddEventTitle(Long chatId, String message) {
        tempEventData.get(chatId).title = message;
        userStates.put(chatId, "ADD_EVENT_DESCRIPTION");
        sendTelegramMessage(chatId, "Введите описание:");
    }

    private void handleAddEventDescription(Long chatId, String message, User user) {
        EventData data = tempEventData.get(chatId);

        try {
            Year yearObj = user.getYear(data.year);
            yearObj.addEvent(data.month, data.day, data.time, data.title, message);

            sendTelegramMessage(chatId, "✅ Дело добавлено!\n" +
                    "📅 " + data.day + "." + data.month + "." + data.year +
                    " ⏰ " + data.time + "\n" +
                    "📝 " + data.title);

        } catch (Exception e) {
            sendTelegramMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }

        userStates.put(chatId, "MAIN_MENU");
        showMainMenu(chatId);
    }

    private void handleViewEventsYear(Long chatId, String message) {
        try {
            int year = Integer.parseInt(message);
            tempEventData.get(chatId).year = year;
            userStates.put(chatId, "VIEW_EVENTS_MONTH");
            sendTelegramMessage(chatId, "Введите месяц (1-12):");
        } catch (NumberFormatException e) {
            sendTelegramMessage(chatId, "Пожалуйста, введите корректный год:");
        }
    }

    private void handleViewEventsMonth(Long chatId, String message) {
        try {
            int month = Integer.parseInt(message);
            tempEventData.get(chatId).month = month;
            userStates.put(chatId, "VIEW_EVENTS_DAY");
            sendTelegramMessage(chatId, "Введите день:");
        } catch (NumberFormatException e) {
            sendTelegramMessage(chatId, "Пожалуйста, введите корректный месяц:");
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
                sendTelegramMessage(chatId, "📭 На " + day + "." + data.month + "." + data.year + " дел нет");
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
                sendTelegramMessage(chatId, eventsText.toString());
            }

        } catch (Exception e) {
            sendTelegramMessage(chatId, "❌ Ошибка: " + e.getMessage());
        }

        userStates.put(chatId, "MAIN_MENU");
        showMainMenu(chatId);
    }

    private void showStatistics(Long chatId, User user) {
        int totalEvents = user.getTotalEvents();
        sendTelegramMessage(chatId,
                "📊 Статистика пользователя " + user.getName() + ":\n" +
                        "👤 ID: " + user.getId() + "\n" +
                        "📈 Всего дел: " + totalEvents);

        userStates.put(chatId, "MAIN_MENU");
        showMainMenu(chatId);
    }
}

class EventData {
    public int year;
    public int month;
    public int day;
    public String time;
    public String title;
}
