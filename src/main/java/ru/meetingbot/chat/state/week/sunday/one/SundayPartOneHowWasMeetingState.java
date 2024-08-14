package ru.meetingbot.chat.state.week.sunday.one;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.dao.FinalMeetingDAO;
import ru.meetingbot.db.model.FinalMeetingModel;

import java.util.List;

public class SundayPartOneHowWasMeetingState extends BaseChatState {

    @Override
    public void onStart() {
        var one = ChatWork.getButton("1", C_ONE);
        var two = ChatWork.getButton("2", C_TWO);
        var three = ChatWork.getButton("3", C_THREE);
        var four = ChatWork.getButton("4", C_FOUR);
        var five = ChatWork.getButton("5", C_FIVE);
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(one, two, three, four, five))
                .build();

        Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartOneHowWasMeetingState.rate"), ParseMode.MARKDOWNV2, keyboard);
        chat.setBotMessageId(send.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        onStart();
    }

    @Override
    public void callbackQuery(String data) {
        short ratio = switch (data) {
            case C_ONE -> 1;
            case C_TWO -> 2;
            case C_THREE -> 3;
            case C_FOUR -> 4;
            case C_FIVE -> 5;
            default -> 0;
        };

        FinalMeetingDAO finalMeetingDAO = new FinalMeetingDAO();
        FinalMeetingModel finalMeetingModel = finalMeetingDAO.get(chat.getUserId()).get();
        finalMeetingModel.setRatio(ratio);
        finalMeetingDAO.update(finalMeetingModel);

        Message message;
        if (ratio < 4) {
            message = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartOneHowWasMeetingState.rate123"), ParseMode.MARKDOWNV2);
        } else {
            message = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartOneHowWasMeetingState.rate45"), ParseMode.MARKDOWNV2);
        }
        chat.setBotMessageId(message.getMessageId());

        ChatWork.changeChatState(chat, ChatState.MAIN);
    }
}
