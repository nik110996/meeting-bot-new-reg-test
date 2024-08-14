package ru.meetingbot.chat.state.week.sunday.two;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.FinalMeetingState;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.model.FinalMeetingModel;

import java.time.LocalDate;
import java.util.List;

public class SundayPartTwoWhoNotHavePartnerState extends BaseChatState {

    @Override
    public void onStart() {
        var yes = ChatWork.getButton(ResBundle.getMessage("sundayPartTwoWhoNotHavePartnerState.yes"), C_ACCEPT);
        var no = ChatWork.getButton(ResBundle.getMessage("sundayPartTwoWhoNotHavePartnerState.no"), C_DECLINE);
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(yes, no))
                .build();

        Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartTwoWhoNotHavePartnerState.ask"), ParseMode.MARKDOWNV2, keyboard);
        chat.setBotMessageId(send.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        onStart();
    }

    @Override
    public void callbackQuery(String data) {
        switch (data) {
            case C_ACCEPT:
                Message accept = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartTwoWhoNotHavePartnerState.response"), ParseMode.MARKDOWNV2, true, true);
                chat.setBotMessageId(accept.getMessageId());
                /* остаётся в meeting "нет пары в среду"*/

                ChatWork.changeChatState(chat, ChatState.MAIN);
                break;

            case C_DECLINE:
                Message decline = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartTwoWhoNotHavePartnerState.skip"), ParseMode.MARKDOWNV2, true, true);
                chat.setBotMessageId(decline.getMessageId());

                /* удалить из meeting */
                new MeetingDAO().deleteById(chat.getUserId());

                /* добавить в final_meeting "отказался" */
                FinalMeetingModel finalMeetingModel = new FinalMeetingModel(chat.getUserId(), FinalMeetingState.REFUSED_TO_MEET.id());
                finalMeetingModel.setDate(LocalDate.now());
                new FinalMeetingDAO().create(finalMeetingModel);

                ChatWork.changeChatState(chat, ChatState.S_TWO_TELL_US_MORE);
                break;
        }
    }
}
