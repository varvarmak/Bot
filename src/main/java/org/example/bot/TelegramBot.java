package org.example.bot;

import org.example.bot.handlers.CommandHandler;
import org.example.bot.handlers.CallbackHandler;
import org.example.bot.handlers.MessageSender;
import org.example.bot.handlers.StateHandler;
import org.example.services.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;

    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private CommandHandler commandHandler;
    private CallbackHandler callbackHandler;
    private MessageSender messageSender;
    private StateHandler stateHandler;

    public TelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        initializeServices();
    }

    private void initializeServices() {
        this.userManager = new UserManager();
        this.stateManager = new UserStateManager();
        this.messageService = new MessageService(this);
        this.messageSender = new MessageSender(this);
        this.commandHandler = new CommandHandler(userManager, stateManager, messageService, messageSender);
        this.callbackHandler = new CallbackHandler(userManager, stateManager, messageService, messageSender);
        this.stateHandler = new StateHandler(userManager, stateManager, messageService, messageSender);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            callbackHandler.handleCallbackQuery(update.getCallbackQuery());
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText().trim();

        if (!userManager.userExists(chatId)) {
            commandHandler.handleStartCommand(chatId, message.getFrom());
            return;
        }

        if (text.startsWith("/")) {
            commandHandler.handleCommand(chatId, text);
            return;
        }

        // ОБРАБАТЫВАЕМ СОСТОЯНИЯ ПОЛЬЗОВАТЕЛЯ
        String state = stateManager.getUserState(chatId);
        stateHandler.handleState(chatId, state, text);
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