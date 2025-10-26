package org.example;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
public class UserManager {
    private Map<Long, User> usersByChatId = new HashMap<>();
    private Map<String, User> usersByName = new HashMap<>();
    private int nextUserId = 1;

    public User getUserByChatId(Long chatId) {
        return usersByChatId.get(chatId);
    }

    public User getUserByName(String name) {
        return usersByName.get(name.toLowerCase());
    }

    public User createUser(Long chatId, String userName) {
        User newUser = new User(nextUserId++, userName);
        usersByChatId.put(chatId, newUser);
        usersByName.put(userName.toLowerCase(), newUser);
        return newUser;
    }

    public void switchUser(Long chatId, User user) {
        usersByChatId.put(chatId, user);
    }

    public void updateUserName(User user, String oldName, String newName) {
        usersByName.remove(oldName.toLowerCase());
        usersByName.put(newName.toLowerCase(), user);
    }

    public String getAvailableUsersList() {
        StringBuilder sb = new StringBuilder();
        for (User user : usersByName.values()) {
            sb.append("• ").append(user.getName())
                    .append(" (ID: ").append(user.getId())
                    .append(", дел: ").append(user.getTotalEvents())
                    .append(")\n");
        }
        return sb.toString().isEmpty() ? "Нет зарегистрированных пользователей" : sb.toString();
    }

    public boolean userExists(Long chatId) {
        return usersByChatId.containsKey(chatId);
    }
    public Long getChatIdByUser(User targetUser) {
        for (Map.Entry<Long, User> entry : usersByChatId.entrySet()) {
            if (entry.getValue().equals(targetUser)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Collection<User> getAllUsers() {
        return usersByName.values();
    }
}
