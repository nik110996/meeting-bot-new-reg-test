package ru.meetingbot.deamon.day;

import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.FinalMeetingState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.chat.ChatWithState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.dao.ChatDAO;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.MeetingModel;
import ru.meetingbot.util.UsersUtils;

import java.time.LocalDate;
import java.util.List;

public class SundayPartTwo implements DBConst {

    private void whoDidNotAnswer() {
        MeetingDAO meetingDAO = new MeetingDAO();
        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();

        //взять всех оставшихся в meeting, кроме "нет пары в среду" и "уже согласен встретиться"
        List<MeetingModel> list = meetingDAO.getWhere(true, T_MEETING_C_MEETING_STATE_ID + "!", MeetingState.W_NOT_HAVE_PARTNER.id(),
                DBConst.T_MEETING_C_MEETING_STATE_ID + "!", MeetingState.S_ACCEPT_MEETING.id());

        //они не ответили, поэтому добавить их в final_meeting
        for (MeetingModel meetingModel : list) {
            long userId = meetingModel.getUserId();
            MeetingState meetingState = MeetingState.stateFromId(meetingModel.getMeetingStateId());
            Long userMeetingId = meetingModel.getUserMeetingId();

            FinalMeetingState finalMeetingState = FinalMeetingState.stateFromMeetingState(0, meetingState);

            FinalMeetingModel finalMeetingModel = new FinalMeetingModel(userId, userMeetingId, finalMeetingState.id(), null, LocalDate.now());
            finalMeetingDAO.create(finalMeetingModel);
        }

        // удалить их из meeting
        meetingDAO.deleteWhere(true,  T_MEETING_C_MEETING_STATE_ID + "!", MeetingState.W_NOT_HAVE_PARTNER.id(),
                DBConst.T_MEETING_C_MEETING_STATE_ID + "!", MeetingState.S_ACCEPT_MEETING.id());
    }

    private void whoOther() {
        /* взять всех из chat, которые не остались в meeting */
        List<ChatWithState> all = new ChatDAO().getAll();

        all.removeIf(chatWithState -> {
            /* убрать из листа тех, кто забанен */
            if(UsersUtils.isBanned(chatWithState.getUserId())) {
                return true;
            }

            /* убрать из листа тех, кто заморожен для участия во встречах */
            if (UsersUtils.isFrozen(chatWithState.getUserId())) {
                return true;
            }

            /* убрать из листа тех, кто заполняет анкету */
            switch (chatWithState.getState()) {
                case START:
                case WRITE_FULL_NAME:
                case WRITE_PROFILE_LINK:
                case WRITE_JOB:
                case WRITE_HOBBIE:
                case WRITE_AGE:
                    return true;
            }

            return false;
        });

        List<MeetingModel> allInMeeting = new MeetingDAO().getAll();
        for (ChatWithState chat : all) {
            boolean exists = false;

            for (MeetingModel meetingModel : allInMeeting) {
                if (meetingModel.getUserId() == chat.getUserId()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                /* будут ли они участвовать во встрече? */
                ChatWork.changeChatState(chat, ChatState.S_TWO_WHO_OTHER);
            }
        }
    }

    private void whoNotHavePartner() {
        MeetingDAO meetingDAO = new MeetingDAO();
        List<MeetingModel> list = meetingDAO.getWhere(true, T_MEETING_C_MEETING_STATE_ID, MeetingState.W_NOT_HAVE_PARTNER.id());
        for (MeetingModel meetingModel : list) {
            ChatWork.changeChatState(meetingModel.getUserId(), ChatState.S_TWO_WHO_NOT_HAVE_PARTNER);
        }
    }

    public void execute() {
        whoDidNotAnswer();
        whoOther();
        whoNotHavePartner();
    }

}
