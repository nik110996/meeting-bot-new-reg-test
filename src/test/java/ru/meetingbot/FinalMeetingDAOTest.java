package ru.meetingbot;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static ru.meetingbot.db.DBConst.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.deamon.day.Monday;
import ru.meetingbot.deamon.day.WednesdayPartTwo;

public class FinalMeetingDAOTest {

    private FinalMeetingDAO finalMeetingDAO;

    @Mock
    private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        finalMeetingDAO = spy(new FinalMeetingDAO());
    }

    @Test
    public void testGetLastMeetingBetweenUsersFound() throws SQLException {
        // Мокируем результат SQL-запроса
        doReturn(resultSet).when(finalMeetingDAO).executeQuery(anyString(), any());

        // Настраиваем поведение ResultSet
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(T_FINAL_MEETING_C_USER_ID)).thenReturn(1L);
        when(resultSet.getLong(T_FINAL_MEETING_C_USER_MEETING_ID)).thenReturn(2L);
        when(resultSet.getShort(T_FINAL_MEETING_C_FINAL_MEETING_STATE_ID)).thenReturn((short) 4);
        when(resultSet.getShort(T_FINAL_MEETING_C_RATIO)).thenReturn((short) 5);
        when(resultSet.getDate(T_FINAL_MEETING_C_DATE_MEETING)).thenReturn(java.sql.Date.valueOf("2023-08-01"));

        // Вызов метода
        Optional<FinalMeetingModel> finalMeeting = finalMeetingDAO.getLastMeetingBetweenUsers(1L, 2L);

        // Проверка результата
        assertTrue(finalMeeting.isPresent());
        assertEquals(1L, finalMeeting.get().getUserId());
        assertEquals(2L, finalMeeting.get().getUserMeetingId().longValue());
        assertEquals(4, finalMeeting.get().getFinalMeetingStateId());
        assertEquals(5, finalMeeting.get().getRatio().intValue());
        assertEquals("2023-08-01", finalMeeting.get().getDate().toString());

        FinalMeetingDAO finalMeetingDAOReal = new FinalMeetingDAO();
        System.out.println(finalMeetingDAOReal.getLastMeetingBetweenUsers(1, 4));

        /*Monday monday = new Monday();
        monday.execute();*/

        WednesdayPartTwo wednesdayPartTwo = new WednesdayPartTwo();
        wednesdayPartTwo.execute();
    }

    @Test
    public void testGetLastMeetingBetweenUsersNotFound() throws SQLException {
        // Мокируем результат SQL-запроса
        doReturn(resultSet).when(finalMeetingDAO).executeQuery(anyString(), any());

        // Настраиваем ResultSet так, чтобы он не возвращал никаких данных
        when(resultSet.next()).thenReturn(false);

        // Вызов метода
        Optional<FinalMeetingModel> finalMeeting = finalMeetingDAO.getLastMeetingBetweenUsers(1L, 2L);

        // Проверка, что результат пустой
        assertFalse(finalMeeting.isPresent());

    }

}
