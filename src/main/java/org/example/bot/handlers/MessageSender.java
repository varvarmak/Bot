package org.example.bot.handlers;

import org.example.bot.TelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSender {
    private TelegramBot bot;

    public MessageSender(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendSimpleMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки: " + e.getMessage());
        }
    }

    public void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка отправки: " + e.getMessage());
        }
    }
}