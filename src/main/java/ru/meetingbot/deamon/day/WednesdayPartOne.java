package ru.meetingbot.deamon.day;

import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.MeetingModel;

import java.util.List;

public class WednesdayPartOne {

    private void whoHavePair() {
        /* взять всех, у кого "есть пара в понедельник" из meeting*/
        List<MeetingModel> list = new MeetingDAO().getWhere(true, DBConst.T_MEETING_C_MEETING_STATE_ID, MeetingState.M_HAVE_PARTNER.id());

        /* договорились о встрече? */
        for (MeetingModel meetingModel : list) {
            ChatWork.changeChatState(meetingModel.getUserId(), ChatState.W_ONE_WHO_HAVE_PAIR);
        }
    }

    public void execute() {
        whoHavePair();
    }
}
