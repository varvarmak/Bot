package org.example.utils;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import static org.junit.Assert.*;

public class UserNameExtractorTest {

    @Test
    public void testExtractUserNameWithFirstNameOnly() {
        User telegramUser = new User();
        telegramUser.setId(12345L);
        telegramUser.setFirstName("Ivan");
        telegramUser.setLastName(null);
        telegramUser.setUserName(null);

        String result = UserNameExtractor.extractUserName(telegramUser);
        assertEquals("Ivan", result);
    }

    @Test
    public void testExtractUserNameWithFirstAndLastName() {
        User telegramUser = new User();
        telegramUser.setId(12345L);
        telegramUser.setFirstName("Ivan");
        telegramUser.setLastName("Ivanov");
        telegramUser.setUserName("ivanov");

        String result = UserNameExtractor.extractUserName(telegramUser);
        assertEquals("Ivan Ivanov", result);
    }

    @Test
    public void testExtractUserNameWithUsernameOnly() {
        User telegramUser = new User();
        telegramUser.setId(12345L);
        telegramUser.setFirstName(""); // Не null, а пустая строка
        telegramUser.setLastName(null);
        telegramUser.setUserName("testuser");

        String result = UserNameExtractor.extractUserName(telegramUser);
        assertEquals("@testuser", result);
    }

    @Test
    public void testExtractUserNameWithNoNameButUsername() {
        User telegramUser = new User();
        telegramUser.setId(12345L);
        telegramUser.setFirstName("");
        telegramUser.setLastName("");
        telegramUser.setUserName("botuser");

        String result = UserNameExtractor.extractUserName(telegramUser);
        assertEquals("@botuser", result);
    }

    @Test
    public void testExtractUserNameWithNoInformation() {
        User telegramUser = new User();
        telegramUser.setId(12345L);
        telegramUser.setFirstName(""); // Не null
        telegramUser.setLastName(null);
        telegramUser.setUserName(null);

        String result = UserNameExtractor.extractUserName(telegramUser);
        assertEquals("User12345", result);
    }

    @Test
    public void testExtractUserNameEmptyFirstName() {
        User telegramUser = new User();
        telegramUser.setId(67890L);
        telegramUser.setFirstName("");
        telegramUser.setLastName("LastName");
        telegramUser.setUserName("username");

        String result = UserNameExtractor.extractUserName(telegramUser);
        assertEquals("@username", result);
    }
}