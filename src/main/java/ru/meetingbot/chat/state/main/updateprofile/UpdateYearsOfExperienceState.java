package ru.meetingbot.chat.state.main.updateprofile;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;

public class UpdateYearsOfExperienceState extends BaseChatState {

    @Override
    public void onStart() {
        String text = ResBundle.getMessage("writeYearsOfExperience.text");

        Message response = ChatWork.sendMessage(chat.getUserId(), text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        short years = 0;

        try {
            years = Short.parseShort(message);
        } catch (NumberFormatException e) {
            years = -1;
        }

        if (years > 0 && years < 200) {
            UserDAO userDAO = new UserDAO();
            UserModel userModel = userDAO.get(chat.getUserId()).get();

            userModel.setYearsOfExperience(years);
            userDAO.update(userModel);

            ChatWork.changeChatState(chat, ChatState.UPDATE_PROFILE);

        } else {
            Message response = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("writeYearsOfExperienceState.congratulation.incorrect"), ParseMode.MARKDOWNV2);
            chat.setBotMessageId(response.getMessageId());

            onStart();
        }
    }

    @Override
    public void callbackQuery(String data) {

    }
}
