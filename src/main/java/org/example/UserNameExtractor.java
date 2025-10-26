package org.example;
import org.telegram.telegrambots.meta.api.objects.User;
public class UserNameExtractor {
    public static String extractUserName(User telegramUser) {
        String firstName = telegramUser.getFirstName() != null ? telegramUser.getFirstName() : "";
        String lastName = telegramUser.getLastName() != null ? telegramUser.getLastName() : "";

        if (!firstName.isEmpty()) {
            return firstName + (lastName.isEmpty() ? "" : " " + lastName);
        }

        String userName = telegramUser.getUserName();
        return userName != null ? "@" + userName : "User" + telegramUser.getId();
    }
}
