package org.example.services;

import org.example.bot.TelegramBot;
import org.example.models.User;
import org.example.models.Year;
import org.example.models.Month;
import org.example.models.Day;
import org.example.models.Event;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Collection;

public class ReminderService {
    private final UserManager userManager;
    private final TelegramBot bot;
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
                    System.out.println("=== НАЧАЛО ПРОВЕРКИ НАПОМИНАНИЙ ===");
                    checkReminders();
                    System.out.println("=== КОНЕЦ ПРОВЕРКИ НАПОМИНАНИЙ ===\n");
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.out.println("Ошибка в напоминаниях: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, "ReminderThread");
        reminderThread.setDaemon(true);
        reminderThread.start();
        System.out.println("✅ ReminderService ЗАПУЩЕН!");
    }

    public void stop() {
        running = false;
    }

    private void checkReminders() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("⏰ Текущее время: " + now);

        Collection<User> allUsers = userManager.getAllUsers();
        System.out.println("👥 Всего пользователей: " + allUsers.size());

        for (User user : allUsers) {
            checkUserReminders(user, now);
        }
    }

    private void checkUserReminders(User user, LocalDateTime now) {
        System.out.println("\nПроверка пользователя: " + user.getName() + " (ID: " + user.getId() + ")");

        LocalDateTime tomorrow = now.plusDays(1);

        System.out.println("Проверяем сегодня: " + now.getDayOfMonth() + "." + now.getMonthValue() + "." + now.getYear());
        checkDayReminders(user, now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now);

        System.out.println("Проверяем завтра: " + tomorrow.getDayOfMonth() + "." + tomorrow.getMonthValue() + "." + tomorrow.getYear());
        checkDayReminders(user, tomorrow.getYear(), tomorrow.getMonthValue(), tomorrow.getDayOfMonth(), now);
    }

    private void checkDayReminders(User user, int year, int month, int day, LocalDateTime now) {
        try {
            Year yearObj = user.getExistingYear(year);
            if (yearObj == null) {
                System.out.println("Год " + year + " не найден у пользователя");
                return;
            }
            System.out.println("Год " + year + " найден");

            Month monthObj = yearObj.getExistingMonth(month);
            if (monthObj == null) {
                System.out.println("Месяц " + month + " не найден");
                return;
            }
            System.out.println("Месяц " + month + " найден");

            Day dayObj = monthObj.getExistingDay(day);
            if (dayObj == null) {
                System.out.println("День " + day + "." + month + "." + year + " не найден");
                return;
            }
            System.out.println("День " + day + "." + month + "." + year + " найден");

            Event[] events = dayObj.getEvents();
            int totalEvents = 0;
            int eventsToRemind = 0;

            for (int i = 0; i < events.length; i++) {
                Event event = events[i];
                if (event != null) {
                    totalEvents++;
                    if (!event.isReminded()) {
                        eventsToRemind++;
                        System.out.println("Событие " + totalEvents + ": " + event.getTime() + " - " + event.getTitle() + " (напомнено: " + event.isReminded() + ")");
                        checkEventReminder(user, event, year, month, day, now);
                    }
                }
            }

            System.out.println("Итого: " + totalEvents + " событий, " + eventsToRemind + " для напоминания");

        } catch (Exception e) {
            System.out.println("Ошибка проверки дня: " + e.getMessage());
            e.printStackTrace();
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

            System.out.println("      ⏱ Время до события '" + event.getTitle() + "': " + minutesUntilEvent + " минут");

            if (minutesUntilEvent <= reminderMinutes && minutesUntilEvent >= 0) {
                System.out.println("      🎯 УСЛОВИЕ ВЫПОЛНЕНО! Отправляем напоминание...");
                sendReminder(user, event, eventTime);
                event.setReminded(true);
                System.out.println("      ✅ Напоминание отправлено и помечено!");
            } else {
                System.out.println("      ⏳ Условие не выполнено. Ждем...");
            }
        } catch (Exception e) {
            System.out.println("Ошибка проверки события: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendReminder(User user, Event event, LocalDateTime eventTime) {
        try {
            Long chatId = userManager.getChatIdByUser(user);
            if (chatId != null) {
                String reminderText = "⏰ НАПОМИНАНИЕ!\n" +
                        "Событие: " + event.getTitle() + "\n" +
                        "Время: " + event.getTime() + "\n" +
                        "Дата: " + eventTime.getDayOfMonth() + "." + eventTime.getMonthValue() + "." + eventTime.getYear() + "\n" +
                        "Описание: " + event.getComm();

                org.example.services.MessageService messageService = new org.example.services.MessageService(bot);
                messageService.sendMessage(chatId, reminderText);
                System.out.println("    Напоминание ОТПРАВЛЕНО в чат " + chatId);
            } else {
                System.out.println("      ОШИБКА: Не найден chatId для пользователя: " + user.getName());
            }
        } catch (Exception e) {
            System.out.println("      ОШИБКА отправки напоминания: " + e.getMessage());
            e.printStackTrace();
        }
    }
}