package org.example;

import org.example.suites.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserRegistrationTest.class,
        CreateEventTest.class,
        ViewEventsTest.class,
        UserStateManagementTest.class,
        ReminderSystemTest.class,
        DatabaseOperationsTest.class,
        UserSwitchingTest.class,
        DataValidationTest.class,
        UserStatisticsTest.class,
        IntegrationTest.class,
        AdditionalServicesTest.class
})
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("ЗАПУСК СЦЕНАРНЫХ ТЕСТОВ TELEGRAM БОТА");

        Result result = JUnitCore.runClasses(TestRunner.class);

        printTestResults(result);
    }

    private static void printTestResults(Result result) {
        System.out.println("\nРЕЗУЛЬТАТЫ ТЕСТИРОВАНИЯ:");
        System.out.println("=" .repeat(50));
        System.out.printf("   Всего тестов: %d\n", result.getRunCount());
        System.out.printf("   Успешных: %d\n", result.getRunCount() - result.getFailureCount());
        System.out.printf("   Проваленных: %d\n", result.getFailureCount());
        System.out.printf("   Время выполнения: %d ms\n", result.getRunTime());
        System.out.println("=" .repeat(50));

        if (result.wasSuccessful()) {
            System.out.println("\nВСЕ ТЕСТЫ ПРОЙДЕНЫ УСПЕШНО!\n");
        } else {
            System.out.println("\nНЕКОТОРЫЕ ТЕСТЫ ПРОВАЛЕНЫ:\n");
            for (Failure failure : result.getFailures()) {
                System.out.println("   ✗ " + failure.getDescription().getMethodName());
                System.out.println("      Класс: " + failure.getDescription().getClassName());
                System.out.println("      Ошибка: " + failure.getMessage());
                System.out.println();
            }
        }

        // Статистика по сценариям
        printScenarioStatistics();
    }

    private static void printScenarioStatistics() {
        System.out.println(" ПРОТЕСТИРОВАННЫЕ СЦЕНАРИИ:");
        System.out.println("=" .repeat(50));
        System.out.println("1.   Регистрация пользователя");
        System.out.println("2.   Создание события");
        System.out.println("3.   Просмотр событий");
        System.out.println("4.   Управление состояниями");
        System.out.println("5.   Система напоминаний");
        System.out.println("6.   Операции с базой данных");
        System.out.println("7.   Смена пользователя");
        System.out.println("8.   Валидация данных");
        System.out.println("9.   Статистика пользователя");
        System.out.println("10.  Интеграционный тест");
        System.out.println("11.  Дополнительные сервисы");
        System.out.println("=" .repeat(50));
    }
}