package ru.meetingbot.chat.state.week.sunday.one;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.FinalMeetingState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.MeetingModel;

import java.time.LocalDate;
import java.util.List;

public class SundayPartOneAllState extends BaseChatState {

    /**
     * возвращает: Ответил ли партнёр, что была встреча?
     * -1 -> не было встречи
     * 0 -> не ответил
     * 1 -> да, встреча была
     */
    private short answerPartnerThatWasMeet(long userId) {
        MeetingModel userMeetingModel = new MeetingDAO().get(userId).get();
        Long partnerId = userMeetingModel.getUserMeetingId();

        if (partnerId == null) {
            return 0;
        }

        List<FinalMeetingModel> partnerFinalMeeting = new FinalMeetingDAO().getWhere(true,
                DBConst.T_FINAL_MEETING_C_USER_ID, partnerId,
                DBConst.T_FINAL_MEETING_C_USER_MEETING_ID, userId,
                DBConst.T_FINAL_MEETING_C_DATE_MEETING, LocalDate.now());

        /* не ответил */
        if (partnerFinalMeeting.isEmpty()) {
            return 0;
        }

        FinalMeetingModel partnerFinalMeetingModel = partnerFinalMeeting.get(0);
        FinalMeetingState partnerFinalMeetingState = FinalMeetingState.stateFromId(partnerFinalMeetingModel.getFinalMeetingStateId());

        boolean wasNotMeeting = FinalMeetingState.wasNotMeeting(partnerFinalMeetingState);
        if (wasNotMeeting) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public void onStart() {
        var yes = ChatWork.getButton(ResBundle.getMessage("sundayPartOneAllState.yes"), C_ACCEPT);
        var no = ChatWork.getButton(ResBundle.getMessage("sundayPartOneAllState.no"), C_DECLINE);
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(yes, no))
                .build();

        Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartOneAllState.ask"), ParseMode.MARKDOWNV2, keyboard);
        chat.setBotMessageId(send.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        onStart();
    }

    @Override
    public void callbackQuery(String data) {
        MeetingDAO meetingDAO = new MeetingDAO();
        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();
        MeetingModel meetingModel = meetingDAO.get(chat.getUserId()).get();
        short answerPartner = answerPartnerThatWasMeet(chat.getUserId());

        /* удалить из meeting */
        meetingDAO.deleteById(chat.getUserId());

        switch (data) {
            case C_ACCEPT:

                /* партнёр ответил, что встречи не было */
                if (answerPartner == -1) {
//                        Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartOneAllState.response"), ParseMode.MARKDOWNV2);
//                        chat.setBotMessageId(send.getMessageId());

                        /* добавить в final_meeting */
                        FinalMeetingModel model = new FinalMeetingModel(chat.getUserId(), FinalMeetingState.NOT_MEET.id());
                        model.setUserMeetingId(meetingModel.getUserMeetingId());
                        model.setDate(LocalDate.now());

                        finalMeetingDAO.create(model);
                        ChatWork.changeChatState(chat, ChatState.S_ONE_HOW_WAS_MEETING);

                /* что встреча была или партнёр ещё не ответил */
                } else {
                    long userId = meetingModel.getUserId();
                    MeetingState meetingState = MeetingState.stateFromId(meetingModel.getMeetingStateId());
                    FinalMeetingState finalMeetingState = FinalMeetingState.stateFromMeetingState(1, meetingState);
                    Long userMeetingId = meetingModel.getUserMeetingId();

                    FinalMeetingModel finalMeetingModel = new FinalMeetingModel(userId, userMeetingId, finalMeetingState.id(), null, LocalDate.now());
                    finalMeetingDAO.create(finalMeetingModel);

                    /* узнать как прошла встреча */
                    ChatWork.changeChatState(chat, ChatState.S_ONE_HOW_WAS_MEETING);
                }

                break;

            case C_DECLINE:
                long userId = meetingModel.getUserId();
                MeetingState meetingState = MeetingState.stateFromId(meetingModel.getMeetingStateId());
                FinalMeetingState finalMeetingState = FinalMeetingState.stateFromMeetingState(-1, meetingState);
                long userMeetingId = meetingModel.getUserMeetingId();

                FinalMeetingModel finalMeetingModel = new FinalMeetingModel(userId, userMeetingId, finalMeetingState.id(), null, LocalDate.now());
                finalMeetingDAO.create(finalMeetingModel);


                /* если партнёр ответил, что встреча была, то отменить это */
                if (answerPartner == 1) {
                    List<FinalMeetingModel> finalMeetingModels = finalMeetingDAO.getWhere(true,
                            DBConst.T_FINAL_MEETING_C_USER_ID, meetingModel.getUserMeetingId(),
                            DBConst.T_FINAL_MEETING_C_USER_MEETING_ID, meetingModel.getUserId(),
                            DBConst.T_FINAL_MEETING_C_DATE_MEETING, LocalDate.now());

                    FinalMeetingModel meetingUserFinalMeetingModel = finalMeetingModels.get(0);
                    FinalMeetingState state = FinalMeetingState.stateFromId(meetingUserFinalMeetingModel.getFinalMeetingStateId());
                    boolean wasMeeting = FinalMeetingState.wasMeeting(state);

                    if (wasMeeting) {
                        meetingUserFinalMeetingModel.setFinalMeetingStateId(FinalMeetingState.NOT_MEET.id());
                        finalMeetingDAO.update(meetingUserFinalMeetingModel);
                    }
                }

                ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartOneAllState.responseNo"), ParseMode.MARKDOWNV2);

                ChatWork.changeChatState(chat, ChatState.S_ONE_TELL_US_MORE);

                break;
        }
    }
}
