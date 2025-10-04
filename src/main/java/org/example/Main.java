package org.example;

public class Main {
    public static void main(String[] args) {

        String botToken = "7824494702:AAH8xCSjLqNXUTIksfACsyzySYeZx9f7K9c";

        TelegramBot bot = new TelegramBot(botToken);
        bot.start();
    }
}