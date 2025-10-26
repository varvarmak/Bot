package org.example;
import org.telegram.telegrambots.bots.TelegramLongPollingBot ;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot  {
    private String botToken;
    private String botUsername;

    private UserManager userManager;
    private UserStateManager stateManager;
    private MessageService messageService;
    private CommandHandler commandHandler;
    private StateHandler stateHandler;
    private ReminderService reminderService;

    public TelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        initializeServices();
    }

    private void initializeServices() {
        this.userManager = new UserManager();
        this.stateManager = new UserStateManager();
        this.messageService = new MessageService(this);
        this.commandHandler = new CommandHandler(userManager, stateManager, messageService);
        this.stateHandler = new StateHandler(userManager, stateManager, messageService);
        this.reminderService = new ReminderService(userManager, this);

        reminderService.start();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        String userName = UserNameExtractor.extractUserName(update.getMessage().getFrom());

        // Инициализация нового пользователя
        if (!userManager.userExists(chatId)) {
            User newUser = userManager.createUser(chatId, userName);
            stateManager.initializeUser(chatId);

            messageService.sendMessage(chatId, "👋 Добро пожаловать, " + userName + "!\n" +
                    "Я буду звать вас: " + userName + "\n\n" +
                    "Если хотите сменить имя, напишите /name");
            messageService.sendMainMenu(chatId, newUser);
            return;
        }

        // Обработка команд
        if (text.startsWith("/")) {
            commandHandler.handleCommand(chatId, text);
            return;
        }

        // Обработка сообщений по состояниям
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