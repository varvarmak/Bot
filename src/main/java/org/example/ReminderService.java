package org.example;

import java.time.Duration;
import java.time.LocalDateTime;

public class ReminderService {
    private UserManager userManager;
    private TelegramBot bot;
    private int reminderMinutes = 1;
    private boolean running = true;

    public ReminderService(UserManager userManager, TelegramBot bot) {
        this.userManager = userManager;
        this.bot = bot;
    }

    public void start() {
        Thread reminderThread = new Thread(() -> {
            while (running) {
                try {
                    checkReminders();
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.out.println("Ошибка в напоминаниях: " + e.getMessage());
                }
            }
        }, "ReminderThread");
        reminderThread.setDaemon(true);
        reminderThread.start();
    }

    public void stop() {
        running = false;
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();


        for (User user : userManager.getAllUsers()) {
            checkUserReminders(user, now);
        }
    }

    private void checkUserReminders(User user, LocalDateTime now) {

        LocalDateTime tomorrow = now.plusDays(1);

        checkDayReminders(user, now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now);
        checkDayReminders(user, tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth(), now);
    }

    private void checkDayReminders(User user, int year, int month, int day, LocalDateTime now) {
        try {
            Year yearObj = user.getExistingYear(year);
            if (yearObj == null) return;

            Month monthObj = yearObj.getExistingMonth(month);
            if (monthObj == null) return;

            Day dayObj = monthObj.getExistingDay(day);
            if (dayObj == null) return;


            for (Event event : dayObj.getEvents()) {
                if (event != null && !event.isReminded()) {
                    checkEventReminder(user, event, year, month, day, now);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка проверки дня: " + e.getMessage());
        }
    }

    private void checkEventReminder(User user, Event event, int year, int month, int day, LocalDateTime now) {
        try {

            String[] timeParts = event.getTime().split(":");
            int eventHour = Integer.parseInt(timeParts[0]);
            int eventMinute = Integer.parseInt(timeParts[1]);

            LocalDateTime eventTime = LocalDateTime.of(year, month, day, eventHour, eventMinute);


            Duration timeUntilEvent = Duration.between(now, eventTime);
            long minutesUntilEvent = timeUntilEvent.toMinutes();

            if (minutesUntilEvent <= reminderMinutes && minutesUntilEvent >= 0) {

                sendReminder(user, event, eventTime);
                event.setReminded(true);
            }
        } catch (Exception e) {
            System.out.println("Ошибка проверки события: " + e.getMessage());
        }
    }

    private void sendReminder(User user, Event event, LocalDateTime eventTime) {

        Long chatId = userManager.getChatIdByUser(user);
        if (chatId != null) {
            String reminderText = "⏰ НАПОМИНАНИЕ!\n" +
                    "Событие: " + event.getTitle() + "\n" +
                    "Время: " + event.getTime() + "\n" +
                    "Дата: " + eventTime.getDayOfMonth() + "." + eventTime.getMonthValue() + "." + eventTime.getYear() + "\n" +
                    "Описание: " + event.getComm();


            MessageService messageService = new MessageService(bot);
            messageService.sendMessage(chatId, reminderText);
        }
    }
}