package ru.meetingbot.chat.state.questionnaire;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;

public class WriteYearsOfExperienceState extends BaseChatState {

    /*private void congratulation(UserModel userModel) {
        String beforeUsername = ResBundle.getMessage("writeYearsOfExperience.beforeUsername");
        String afterUsername = ResBundle.getMessage("writeYearsOfExperience.afterUsername");
        String congratulation = ResBundle.getMessage("writeYearsOfExperience.congratulation");
        String congratulationTwo = ResBundle.getMessage("writeYearsOfExperience.congratulation2");

        String username = StringMarkdownV2.getString(userModel.getUserName());
        String fullName = StringMarkdownV2.getString(userModel.getFullName());
        String profileLink = StringMarkdownV2.getString(userModel.getProfileLink());
        String instagram = "[" + profileLink + "]" + "(https://www.instagram.com/" + profileLink + "/)";
        String job = StringMarkdownV2.getString(userModel.getJob());
        String hobbie = StringMarkdownV2.getString(userModel.getHobbie());
        Short age = userModel.getYearsOfExperience();

        String separator = ResBundle.getMessage("writeYearsOfExperienceState.congratulation.separator");
        String text = new StringBuilder()
                .append(beforeUsername).append(username).append(afterUsername)
                .append(congratulation)
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.name")).append(separator).append(fullName).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.profileLink")).append(separator).append(instagram).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.job")).append(separator).append(job).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.hobbie")).append(separator).append(hobbie).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.location")).append(separator).append(hobbie).append("\n")
                .append(ResBundle.getMessage("writeYearsOfExperienceState.congratulation.yearsOfExperienceState.congratulation")).append(separator).append(age).append("\n")
                .append(congratulationTwo).toString();

        Message response = ChatWork.sendMessage(chat.getUserId(), text, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(response.getMessageId());

        String congratulationThree = ResBundle.getMessage("writeYearsOfExperience.congratulation3");

        Message next = ChatWork.sendMessage(chat.getUserId(), congratulationThree, ParseMode.MARKDOWNV2);
        chat.setBotMessageId(next.getMessageId());
    }*/

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

            //congratulation(userModel);

            ChatWork.changeChatState(chat, ChatState.WRITE_LOCATION);

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
