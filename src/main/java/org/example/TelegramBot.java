package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class TelegramBot extends TelegramLongPollingBot {
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

        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
            return;
        }


        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        String userName = UserNameExtractor.extractUserName(update.getMessage().getFrom());


        if (!userManager.userExists(chatId)) {
            User newUser = userManager.createUser(chatId, userName);
            stateManager.initializeUser(chatId);

            messageService.sendMessage(chatId, "👋 Добро пожаловать, " + userName + "!\n" +
                    "Я буду звать вас: " + userName + "\n\n" +
                    "Если хотите сменить имя, напишите /name");
            messageService.sendMainMenu(chatId, newUser);
            return;
        }


        if (text.startsWith("/")) {
            commandHandler.handleCommand(chatId, text);
            return;
        }


        String state = stateManager.getUserState(chatId);
        stateHandler.handleState(chatId, state, text);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData(); // данные с кнопки
        String state = stateManager.getUserState(chatId);

        EventData tempData = stateManager.getTempEventData(chatId);

        switch (state) {
            case "ADD_EVENT_MONTH":

                int monthNumber = convertMonthNameToNumber(data);
                if (monthNumber != -1) {
                    tempData.month = monthNumber;
                    stateManager.setUserState(chatId, "ADD_EVENT_DAY");
                    messageService.sendDayButtons(chatId, tempData.year, monthNumber);
                } else {
                    messageService.sendMessage(chatId, "❌ Не удалось определить месяц. Попробуйте ещё раз.");
                    messageService.sendMonthButtons(chatId);
                }
                break;

            case "ADD_EVENT_DAY":
                try {
                    int dayNumber = Integer.parseInt(data);
                    tempData.day = dayNumber;
                    stateManager.setUserState(chatId, "ADD_EVENT_TIME");
                    messageService.sendMessage(chatId, "Введите время события (HH:MM):");
                } catch (NumberFormatException e) {
                    messageService.sendMessage(chatId, "❌ Некорректный день. Попробуйте снова.");
                    messageService.sendDayButtons(chatId, tempData.year, tempData.month);
                }
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

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
