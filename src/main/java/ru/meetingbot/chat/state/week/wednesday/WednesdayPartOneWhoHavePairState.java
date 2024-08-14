package ru.meetingbot.chat.state.week.wednesday;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.MeetingState;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.MeetingModel;

import java.util.List;

public class WednesdayPartOneWhoHavePairState extends BaseChatState {

    @Override
    public void onStart() {
        var yes = ChatWork.getButton(ResBundle.getMessage("wednesdayPartOneWhoHavePairState.yes"), C_ACCEPT);
        var no = ChatWork.getButton(ResBundle.getMessage("wednesdayPartOneWhoHavePairState.no"), C_DECLINE);
        var willTryAgain = ChatWork.getButton(ResBundle.getMessage("wednesdayPartOneWhoHavePairState.maybe"), C_WILL_TRY_AGAIN);
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(yes, no))
                .keyboardRow(List.of(willTryAgain))
                .build();

        Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("wednesdayPartOneWhoHavePairState.ask"), ParseMode.MARKDOWNV2, keyboard);
        chat.setBotMessageId(send.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        onStart();
    }

    @Override
    public void callbackQuery(String data) {
        MeetingDAO meetingDAO = new MeetingDAO();
        MeetingModel meetingModel = meetingDAO.get(chat.getUserId()).get();

        String response = "";
        switch (data) {
            case C_ACCEPT:
                meetingModel.setMeetingStateId(MeetingState.W_ALREADY_AGREED.id());
                response = ResBundle.getMessage("wednesdayPartOneWhoHavePairState.responseYes");
                break;
            case C_DECLINE:
                meetingModel.setMeetingStateId(MeetingState.W_NOT_HAVE_PARTNER.id());
                response = ResBundle.getMessage("wednesdayPartOneWhoHavePairState.responseNo");
                break;
            case C_WILL_TRY_AGAIN:
                meetingModel.setMeetingStateId(MeetingState.W_TRYING_TO_NEGOTIATE.id());
                response = ResBundle.getMessage("wednesdayPartOneWhoHavePairState.responseMaybe");
                break;
        }
        //* обновить состояния в meeting */
        meetingDAO.update(meetingModel);

        Message send = ChatWork.sendMessage(chat.getUserId(), response, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(send.getMessageId());

        ChatWork.changeChatState(chat, ChatState.MAIN);
    }
}
