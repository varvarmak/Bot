package org.example.mocks;

import org.example.bot.handlers.StateHandler;
import org.example.services.*;
import org.example.models.User;
import org.example.models.EventData;
import org.example.models.Year;
import org.example.models.Month;
import org.example.models.Day;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MockTelegramTest {

    @Mock
    private UserManager mockUserManager;

    @Mock
    private UserStateManager mockStateManager;

    @Mock
    private MessageService mockMessageService;

    @Mock
    private org.example.bot.handlers.MessageSender mockMessageSender;

    @Mock
    private User mockUser;

    @Mock
    private Year mockYear;

    @Mock
    private Month mockMonth;

    @Mock
    private Day mockDay;

    private StateHandler stateHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        stateHandler = new StateHandler(mockUserManager, mockStateManager, mockMessageService, mockMessageSender);
    }

    @Test
    public void testMockAddEventFlow() {
        // Настройка моков
        Long chatId = 12345L;
        when(mockUserManager.getUserByChatId(chatId)).thenReturn(mockUser);
        when(mockStateManager.getUserState(chatId)).thenReturn("ADD_EVENT_DESCRIPTION");

        EventData mockEventData = new EventData();
        mockEventData.year = 2025;
        mockEventData.month = 6;
        mockEventData.day = 15;
        mockEventData.time = "14:30";
        mockEventData.title = "Тестовая встреча";

        when(mockStateManager.getTempEventData(chatId)).thenReturn(mockEventData);

        // Настраиваем цепочку вызовов для добавления события
        when(mockUser.getYear(2025)).thenReturn(mockYear);

        // Выполняем обработку описания
        stateHandler.handleState(chatId, "ADD_EVENT_DESCRIPTION", "Тестовое описание");

        // Проверяем вызовы
        verify(mockMessageSender, atLeastOnce()).sendSimpleMessage(eq(chatId), anyString());
        verify(mockUser).getYear(2025);
        verify(mockYear).addEvent(eq(6), eq(15), eq("14:30"), eq("Тестовая встреча"), eq("Тестовое описание"));
        verify(mockStateManager).clearTempEventData(chatId);
        verify(mockStateManager).setUserState(chatId, "MAIN_MENU");
    }

    @Test
    public void testMockMainMenu() {
        Long chatId = 12346L;
        when(mockUserManager.getUserByChatId(chatId)).thenReturn(mockUser);
        when(mockStateManager.getUserState(chatId)).thenReturn("MAIN_MENU");

        // Тестируем разные команды главного меню
        stateHandler.handleState(chatId, "MAIN_MENU", "📅 Добавить дело");
        verify(mockStateManager).setUserState(chatId, "ADD_EVENT_YEAR");
        verify(mockMessageSender).sendSimpleMessage(eq(chatId), contains("год"));
    }

    @Test
    public void testMockInvalidTime() {
        Long chatId = 12347L;

        EventData mockEventData = new EventData();
        when(mockStateManager.getTempEventData(chatId)).thenReturn(mockEventData);
        when(mockStateManager.getUserState(chatId)).thenReturn("ADD_EVENT_TIME");

        // Тестируем неверное время
        stateHandler.handleState(chatId, "ADD_EVENT_TIME", "25:00");

        verify(mockMessageSender).sendSimpleMessage(eq(chatId), contains("Неверный формат"));
    }

    @Test
    public void testMockViewEvents() {
        Long chatId = 12348L;
        when(mockUserManager.getUserByChatId(chatId)).thenReturn(mockUser);
        when(mockStateManager.getUserState(chatId)).thenReturn("VIEW_EVENTS_DAY");

        EventData mockEventData = new EventData();
        mockEventData.year = 2025;
        mockEventData.month = 6;
        when(mockStateManager.getTempEventData(chatId)).thenReturn(mockEventData);

        // Тестируем просмотр событий
        stateHandler.handleState(chatId, "VIEW_EVENTS_DAY", "15");

        verify(mockMessageSender, atLeastOnce()).sendSimpleMessage(eq(chatId), anyString());
    }
}