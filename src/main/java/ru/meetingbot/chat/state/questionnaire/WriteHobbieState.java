package ru.meetingbot.chat.state.questionnaire;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;

public class WriteHobbieState extends BaseChatState {

    @Override
    public void onStart() {
        String text = ResBundle.getMessage("writeHobbie.text");

        Message response = ChatWork.sendPhoto(chat.getUserId(), "pics/writeHobbie.jpg", text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        if (message.length() < 250) {
            UserDAO userDAO = new UserDAO();
            UserModel userModel = userDAO.get(chat.getUserId()).get();

            userModel.setHobbie(message);
            userDAO.update(userModel);

            ChatWork.changeChatState(chat, ChatState.WRITE_YEARS_OF_EXPERIENCE);
        } else {
            Message response = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("writeHobbie.incorrect"), ParseMode.MARKDOWNV2);
            chat.setBotMessageId(response.getMessageId());

            onStart();
        }
    }

    @Override
    public void callbackQuery(String data) {

    }
}
