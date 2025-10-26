package org.example;
import java.util.HashMap;
import java.util.Map;
public class UserStateManager {
    private Map<Long, String> userStates = new HashMap<>();
    private Map<Long, EventData> tempEventData = new HashMap<>();

    public String getUserState(Long chatId) {
        return userStates.getOrDefault(chatId, "MAIN_MENU");
    }

    public void setUserState(Long chatId, String state) {
        userStates.put(chatId, state);
    }

    public EventData getTempEventData(Long chatId) {
        return tempEventData.computeIfAbsent(chatId, k -> new EventData());
    }

    public void clearTempEventData(Long chatId) {
        tempEventData.remove(chatId);
    }

    public void initializeUser(Long chatId) {
        userStates.put(chatId, "MAIN_MENU");
        tempEventData.put(chatId, new EventData());
    }

}
