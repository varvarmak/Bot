package org.example.bot.handlers;

import org.example.services.UserManager;
import org.example.services.UserStateManager;
import org.example.services.MessageService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class CallbackHandler {
    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private MessageSender messageSender;

    public CallbackHandler(UserManager userManager, UserStateManager stateManager,
                           MessageService messageService, MessageSender messageSender) {
        this.userManager = userManager;
        this.stateManager = stateManager;
        this.messageService = messageService;
        this.messageSender = messageSender;
    }

    public void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        String state = stateManager.getUserState(chatId);

        if (data.startsWith("horoscope_")) {
            String zodiacSign = data.substring("horoscope_".length());
            messageService.sendHoroscope(chatId, zodiacSign);
            return;
        }


        if (data.startsWith("weather_")) {
            String city = null;
            if (data.equals("weather_ekaterinburg")) {
                city = "Ekaterinburg";
            } else if (data.equals("weather_moscow")) {
                city = "Moscow";
            }
            if (city != null) {
                messageService.sendWeather(chatId, city);
            }
            return;
        }

        org.example.models.EventData tempData = stateManager.getTempEventData(chatId);
        switch (state) {
            case "ADD_EVENT_MONTH":
                int monthNumber = convertMonthNameToNumber(data);
                if (monthNumber != -1) {
                    tempData.month = monthNumber;
                    stateManager.setUserState(chatId, "ADD_EVENT_DAY");
                    messageService.sendDayButtons(chatId, tempData.year, monthNumber);
                }
                break;

            case "ADD_EVENT_DAY":
                try {
                    int dayNumber = Integer.parseInt(data);
                    tempData.day = dayNumber;
                    stateManager.setUserState(chatId, "ADD_EVENT_TIME");
                    messageSender.sendSimpleMessage(chatId, "Введите время события (HH:MM):");
                } catch (NumberFormatException e) {
                    messageSender.sendSimpleMessage(chatId, "❌ Некорректный день");
                }
                break;

            default:
                org.example.models.User user = userManager.getUserByChatId(chatId);
                stateManager.setUserState(chatId, "MAIN_MENU");
                stateManager.clearTempEventData(chatId);
                messageService.sendMainMenu(chatId, user);
                break;
        }
    }

    private int convertMonthNameToNumber(String monthName) {
        switch (monthName) {
            case "Январь": return 1;
            case "Февраль": return 2;
            case "Март": return 3;
            case "Апрель": return 4;
            case "Май": return 5;
            case "Июнь": return 6;
            case "Июль": return 7;
            case "Август": return 8;
            case "Сентябрь": return 9;
            case "Октябрь": return 10;
            case "Ноябрь": return 11;
            case "Декабрь": return 12;
            default: return -1;
        }
    }
}