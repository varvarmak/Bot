package org.example;

public class CommandHandler {
    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;

    public CommandHandler(UserManager userManager, UserStateManager stateManager, MessageService messageService) {
        this.userManager = userManager;
        this.stateManager = stateManager;
        this.messageService = messageService;
    }

    public void handleCommand(Long chatId, String command) {
        switch (command) {
            case "/start":
                messageService.sendMainMenu(chatId, userManager.getUserByChatId(chatId));
                break;
            case "/name":
                stateManager.setUserState(chatId, "CHANGE_USER_NAME");
                messageService.sendMessage(chatId, "✏️ Введите новое имя:");
                break;
            case "/switch":
                stateManager.setUserState(chatId, "SWITCH_USER");
                messageService.sendMessage(chatId, "🔄 Введите имя пользователя:");
                break;
            case "/stats":
                messageService.sendStatistics(chatId, userManager.getUserByChatId(chatId));
                break;
            case "/help":
                messageService.sendHelpMessage(chatId);
                break;
            default:
                messageService.sendMessage(chatId, "❌ Неизвестная команда. Используйте /help");
        }
    }
}
