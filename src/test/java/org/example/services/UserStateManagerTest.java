package org.example.services;

import org.example.models.EventData;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserStateManagerTest {

    private UserStateManager stateManager;
    private Long chatId;

    @Before
    public void setUp() {
        stateManager = new UserStateManager();
        chatId = 12345L;
    }

    @Test
    public void testGetUserStateDefault() {
        assertEquals("MAIN_MENU", stateManager.getUserState(chatId));
    }

    @Test
    public void testSetAndGetUserState() {
        stateManager.setUserState(chatId, "ADD_EVENT_TITLE");
        assertEquals("ADD_EVENT_TITLE", stateManager.getUserState(chatId));
    }

    @Test
    public void testGetTempEventDataCreatesNew() {
        EventData data1 = stateManager.getTempEventData(chatId);
        EventData data2 = stateManager.getTempEventData(chatId);

        assertNotNull(data1);
        assertNotNull(data2);
        assertSame(data1, data2); // Should return same object
    }

    @Test
    public void testClearTempEventData() {
        EventData data = stateManager.getTempEventData(chatId);
        assertNotNull(data);

        stateManager.clearTempEventData(chatId);
        EventData newData = stateManager.getTempEventData(chatId);
        assertNotNull(newData);
        assertNotSame(data, newData); // Should be new object
    }

    @Test
    public void testInitializeUser() {
        stateManager.initializeUser(chatId);
        assertEquals("MAIN_MENU", stateManager.getUserState(chatId));
        assertNotNull(stateManager.getTempEventData(chatId));
    }

    @Test
    public void testMultipleUsers() {
        Long chatId1 = 11111L;
        Long chatId2 = 22222L;

        stateManager.setUserState(chatId1, "STATE_1");
        stateManager.setUserState(chatId2, "STATE_2");

        EventData data1 = stateManager.getTempEventData(chatId1);
        data1.year = 2024;

        EventData data2 = stateManager.getTempEventData(chatId2);
        data2.year = 2025;

        assertEquals("STATE_1", stateManager.getUserState(chatId1));
        assertEquals("STATE_2", stateManager.getUserState(chatId2));
        assertEquals(2024, stateManager.getTempEventData(chatId1).year);
        assertEquals(2025, stateManager.getTempEventData(chatId2).year);
    }
}