package ru.meetingbot.chat.state.main.updateprofile;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.util.StringMarkdownV2;

public class UpdateAgeState extends BaseChatState {

    @Override
    public void onStart() {
        String text = ResBundle.getMessage("writeAge.text");

        Message response = ChatWork.sendMessage(chat.getUserId(), text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        short age = 0;

        try {
            age = Short.parseShort(message);
        } catch (NumberFormatException e) {
            age = -1;
        }

        if (age > 0 && age < 200) {
            UserDAO userDAO = new UserDAO();
            UserModel userModel = userDAO.get(chat.getUserId()).get();

            userModel.setAge(age);
            userDAO.update(userModel);

            ChatWork.changeChatState(chat, ChatState.UPDATE_PROFILE);

        } else {
            Message response = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("writeAgeState.congratulation.incorrect"), ParseMode.MARKDOWNV2);
            chat.setBotMessageId(response.getMessageId());

            onStart();
        }
    }

    @Override
    public void callbackQuery(String data) {

    }
}
