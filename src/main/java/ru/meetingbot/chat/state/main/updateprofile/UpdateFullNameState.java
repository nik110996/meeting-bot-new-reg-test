package ru.meetingbot.chat.state.main.updateprofile;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;

public class UpdateFullNameState extends BaseChatState {

    @Override
    public void onStart() {
        String text = ResBundle.getMessage("writeFullName.text");

        Message response = ChatWork.sendPhoto(chat.getUserId(), "pics/writeFullName.jpg", text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());
    }

    @Override
    public void writeMessage(String message) {

        if (ChatWork.regExp(message, REG_EXP_NAME, 128)) {
            UserDAO userDAO = new UserDAO();
            UserModel userModel = userDAO.get(chat.getUserId()).get();

            userModel.setFullName(message);
            userDAO.update(userModel);

            ChatWork.changeChatState(chat, ChatState.UPDATE_PROFILE);
        } else {
            Message response = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("writeFullNameState.invalid"), ParseMode.MARKDOWNV2);
            chat.setBotMessageId(response.getMessageId());

            onStart();
        }
    }

    @Override
    public void callbackQuery(String data) {

    }
}
