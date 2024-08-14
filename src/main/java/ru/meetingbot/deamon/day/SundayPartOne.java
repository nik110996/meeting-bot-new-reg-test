package ru.meetingbot.deamon.day;

import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.MeetingModel;

import java.util.List;

public class SundayPartOne implements DBConst {

    private void whoAll() {
        MeetingDAO meetingDAO = new MeetingDAO();

        //взять всех в meeting, кроме тех "нет пары в среду"
        List<MeetingModel> list = meetingDAO.getWhere(true,
                T_MEETING_C_MEETING_STATE_ID + "!", MeetingState.W_NOT_HAVE_PARTNER.id());

        // спросить "Получилось ли встретиться?"
        for (MeetingModel meetingModel : list) {
            ChatWork.changeChatState(meetingModel.getUserId(), ChatState.S_ONE_ALL);
        }
    }

    public void execute() {
        whoAll();
    }
}
