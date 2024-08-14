package ru.meetingbot.chat.state.questionnaire;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.util.StringMarkdownV2;

public class WriteAgeState extends BaseChatState {

    private void congratulation(UserModel userModel) {
        String beforeUsername = ResBundle.getMessage("writeAge.beforeUsername");
        String afterUsername = ResBundle.getMessage("writeAge.afterUsername");
        String congratulation = ResBundle.getMessage("writeAge.congratulation");
        String congratulationTwo = ResBundle.getMessage("writeAge.congratulation2");

        String username = StringMarkdownV2.getString(userModel.getUserName());
        String fullName = StringMarkdownV2.getString(userModel.getFullName());
        String profileLink = StringMarkdownV2.getString(userModel.getProfileLink());
        String instagram = "[" + profileLink + "]" + "(https://www.instagram.com/" + profileLink + "/)";
        String job = StringMarkdownV2.getString(userModel.getJob());
        String hobbie = StringMarkdownV2.getString(userModel.getHobbie());
        Short age = userModel.getAge();

        String separator = ResBundle.getMessage("writeAgeState.congratulation.separator");
        String text = new StringBuilder()
                .append(beforeUsername).append(username).append(afterUsername)
                .append(congratulation)
                .append(ResBundle.getMessage("writeAgeState.congratulation.name")).append(separator).append(fullName).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.profileLink")).append(separator).append(instagram).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.job")).append(separator).append(job).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.hobbie")).append(separator).append(hobbie).append("\n")
                .append(ResBundle.getMessage("writeAgeState.congratulation.age")).append(separator).append(age).append("\n")
                .append(congratulationTwo).toString();

        Message response = ChatWork.sendMessage(chat.getUserId(), text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());

        String congratulationThree = ResBundle.getMessage("writeAge.congratulation3");

        Message next = ChatWork.sendMessage(chat.getUserId(), congratulationThree, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(next.getMessageId());
    }

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

            congratulation(userModel);

            ChatWork.changeChatState(chat, ChatState.MAIN);

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
