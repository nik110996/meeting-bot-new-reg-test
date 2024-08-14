package ru.meetingbot.chat.state.main;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.MeetingModel;
import ru.meetingbot.util.StringMarkdownV2;

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

        ChatWork.sendMessage(chat.getUserId(), response, ParseMode.MARKDOWNV2);
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
            default -> {}
        }
    }

    @Override
    public void callbackQuery(String data) {}
}
