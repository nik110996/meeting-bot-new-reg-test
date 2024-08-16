package ru.meetingbot.chat.state.main;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.MeetingModel;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.util.StringMarkdownV2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * изменить анкету
 * с кем встреча
 * показать статистику встреч
 */
public class MainState extends BaseChatState {

    private void whoIsMyMeetingWith() {
        String response = "";

        Optional<MeetingModel> optional = new MeetingDAO().get(chat.getUserId());
        if (optional.isPresent()) {
            MeetingModel meetingModel = optional.get();
            Long userMeetingId = meetingModel.getUserMeetingId();
            if (userMeetingId != null) {
                String userName = new UserDAO().get(userMeetingId).get().getUserName();
                response = ResBundle.getMessage("mainState.havePartner") + StringMarkdownV2.getString(userName);
            } else {
                response = ResBundle.getMessage("mainState.dontHavePartner");
            }
        } else {
            response = ResBundle.getMessage("mainState.dontHaveMeeting");
        }
        System.out.println(response);
        ChatWork.sendMessage(chat.getUserId(), response, ParseMode.MARKDOWNV2);
    }

    private void whatAreMyPreviousMatches(long userId) {
        StringBuilder response = new StringBuilder();
        List<Optional<FinalMeetingModel>> list = new FinalMeetingDAO().getMyPreviousMatches(userId);
        long partnerId = 0;
        long oldPartnerId = 0;
        LocalDate oldDate = null;

        for (Optional<FinalMeetingModel> meetingModelOptional : list) {
            if (meetingModelOptional.isPresent()) {
                FinalMeetingModel meetingModel = meetingModelOptional.get();


                if (meetingModel.getUserId() == userId) {
                    partnerId = meetingModel.getUserMeetingId();
                }

                if (meetingModel.getUserMeetingId() == userId) {
                    partnerId = meetingModel.getUserId();
                }

                if (partnerId == oldPartnerId && meetingModel.getDate().equals(oldDate)) {
                    continue;
                }

                oldPartnerId = partnerId;
                oldDate = meetingModel.getDate();
                Optional<UserModel> partnerModel = new UserDAO().get(partnerId);

                if (partnerModel.isPresent()) {
                    UserModel userModel = partnerModel.get();
                    response.append(userModel.getUserName())
                            .append(" ")
                            .append(meetingModel.getDate().toString())
                            .append("\n");
                }
            }
        }

        String answer = StringMarkdownV2.getString(response.toString());
        ChatWork.sendMessage(userId, answer, ParseMode.MARKDOWNV2);
    }

    @Override
    public void onStart() {
//        Message begin = ChatWork.sendMessage(chat.getUserId(), "_MainState_", ParseMode.MARKDOWNV2);
//        chat.setBotMessageId(begin.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        switch (message) {
            case C_UPDATE_PROFILE -> ChatWork.changeChatState(chat.getUserId(), ChatState.UPDATE_PROFILE);
            case C_WHO_IS_MY_MEETING_WITH -> whoIsMyMeetingWith();
            case C_HELP -> {
                ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("mainState.help"), ParseMode.MARKDOWNV2);
            }
            case C_PREVIOUS_MATCHES -> whatAreMyPreviousMatches(chat.getUserId());
            case C_HOW_TO_UNSUBSCRIBE -> ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("mainState.unsubscribe"), ParseMode.MARKDOWNV2);
            default -> {}
        }
    }

    @Override
    public void callbackQuery(String data) {}
}
