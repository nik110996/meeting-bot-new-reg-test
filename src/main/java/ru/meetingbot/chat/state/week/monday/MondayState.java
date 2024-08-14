package ru.meetingbot.chat.state.week.monday;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.MeetingModel;

import java.util.List;
import java.util.Optional;

public class MondayState extends BaseChatState {

    private InlineKeyboardMarkup getKeyboard() {
        var button = ChatWork.getButton(ResBundle.getMessage("monday.button"), C_WANT_ON_THIS_WEEK);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build();
    }

    @Override
    public void onStart() {
        Message message = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("monday.skipWhoNotHavePartner"), ParseMode.MARKDOWNV2, getKeyboard());
        chat.setBotMessageId(message.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        ChatWork.changeChatState(chat.getUserId(), ChatState.MAIN, message);
    }

    @Override
    public void callbackQuery(String data) {
        long userId = chat.getUserId();

        switch (data) {
            case C_WANT_ON_THIS_WEEK:
                MeetingDAO meetingDAO = new MeetingDAO();
                Optional<MeetingModel> optionalMeetingModel = meetingDAO.get(userId);
                if (optionalMeetingModel.isPresent()) {
                    MeetingModel meetingModel = optionalMeetingModel.get();
                    meetingModel.setMeetingStateId(MeetingState.M_NOT_HAVE_PARTNER.id());
                    meetingModel.setUserMeetingId(null);
                    meetingDAO.update(meetingModel);
                } else {
                    MeetingModel meetingModel = new MeetingModel(userId, MeetingState.M_NOT_HAVE_PARTNER.id(), null);
                    meetingDAO.create(meetingModel);
                }
        }

        ChatWork.changeChatState(userId, ChatState.MAIN);
    }
}
