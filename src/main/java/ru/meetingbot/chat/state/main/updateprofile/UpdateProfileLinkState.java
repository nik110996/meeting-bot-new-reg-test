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

public class UpdateProfileLinkState extends BaseChatState {

    @Override
    public void onStart() {
        String beforeUsername= ResBundle.getMessage("writeProfileLink.beforeUsername");
        String afterUsername= ResBundle.getMessage("writeProfileLink.afterUsername");
        String username = new UserDAO().get(chat.getUserId()).get().getUserName();
        String text = beforeUsername + StringMarkdownV2.getString(username) + afterUsername;

        Message response = ChatWork.sendMessage(chat.getUserId(), text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        if (message.length() < 250 && !message.startsWith("@")) {
            UserDAO userDAO = new UserDAO();
            UserModel userModel = userDAO.get(chat.getUserId()).get();

            userModel.setProfileLink(message);
            userDAO.update(userModel);

            ChatWork.changeChatState(chat, ChatState.UPDATE_PROFILE);
        } else {
            Message response = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("writeProfileLink.incorrect"), ParseMode.MARKDOWNV2);
            chat.setBotMessageId(response.getMessageId());

            onStart();
        }
    }

    @Override
    public void callbackQuery(String data) {

    }
}
