package org.example;

import org.example.bot.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) throws Exception {
        String botToken = "7824494702:AAH8xCSjLqNXUTIksfACsyzySYeZx9f7K9c";
        String botUsername = "@ezhdnevki_bot";

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new TelegramBot(botToken, botUsername));
        System.out.println("✅ Бот запущен!");
    }
}