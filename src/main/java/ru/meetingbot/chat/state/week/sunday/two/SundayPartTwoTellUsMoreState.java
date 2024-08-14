package ru.meetingbot.chat.state.week.sunday.two;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.chat.state.week.sunday.one.SundayPartOneTellUsMoreState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;

import java.util.logging.Logger;

public class SundayPartTwoTellUsMoreState extends BaseChatState {

    private static final Logger logger = Logger.getLogger(SundayPartTwoTellUsMoreState.class.getName());

    @Override
    public void onStart() {

    }

    @Override
    public void writeMessage(String message) {
        UserModel userModel = new UserDAO().get(chat.getUserId()).get();
        String string = new StringBuilder()
                .append("Почему отказался от участия во встречах @").append(userModel.getUserName())
                .append(" (").append(userModel.getId()).append("): ")
                .append(message).toString();

        logger.info(string);

        Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("sundayPartTwoTellUsMoreState.response"), ParseMode.MARKDOWNV2);
        chat.setBotMessageId(send.getMessageId());

        ChatWork.changeChatState(chat, ChatState.MAIN);
    }

    @Override
    public void callbackQuery(String data) {

    }
}
