package ru.meetingbot.chat.state.main;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.consts.UnicodeSymbol;
import ru.meetingbot.chat.state.BaseChatState;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.model.UserModel;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateProfileState extends BaseChatState {

    private static final Logger logger = Logger.getLogger(UpdateProfileState.class.getName());

    private InlineKeyboardMarkup getRegIkm() {
        UserModel userModel = new UserDAO().get(chat.getUserId()).get();

//        boolean isEmptyFullName = userModel.getFullName().equals("");
//        boolean isEmptyProfileLink = userModel.getProfileLink().equals("");
//        boolean isEmptyJob = userModel.getJob().equals("");
//        boolean isEmptyHobbie = userModel.getHobbie().equals("");
//        boolean isEmptyAge = (userModel.getYearsOfExperience() == 0);

        String separator = ResBundle.getMessage("updateProfileState.separator");
        String fullName = /*isEmptyFullName ? U_RED_CIRCLE + " Name" : U_GREEN_CIRCLE + */ResBundle.getMessage("updateProfileState.name") + separator + userModel.getFullName();
        String profileLink = /*isEmptyProfileLink ? U_YELLOW_CIRCLE + " Social Network" : U_GREEN_CIRCLE + */ResBundle.getMessage("updateProfileState.profileLink") + separator + userModel.getProfileLink();
        String job = /*isEmptyJob ? U_YELLOW_CIRCLE + " Job" : U_GREEN_CIRCLE + */ResBundle.getMessage("updateProfileState.job") + separator + userModel.getJob();
        String hobbie = /*isEmptyHobbie ? U_YELLOW_CIRCLE + " Hobbie" : U_GREEN_CIRCLE + */ResBundle.getMessage("updateProfileState.hobbie") + separator + userModel.getHobbie();
        String age = /*isEmptyAge ? U_YELLOW_CIRCLE + " Age" : U_GREEN_CIRCLE + */ResBundle.getMessage("updateProfileState.yearsOfExperience") + separator + userModel.getYearsOfExperience();

        var fullNameButton = ChatWork.getButton(fullName, C_UPDATE_FULL_NAME);
        var profileLinkButton = ChatWork.getButton(profileLink, C_UPDATE_PROFILE_LINK);
        var jobButton = ChatWork.getButton(job, C_UPDATE_JOB);
        var hobbieButton = ChatWork.getButton(hobbie, C_UPDATE_HOBBIE);
        var ageButton = ChatWork.getButton(age, C_UPDATE_AGE);

        var acceptButton = ChatWork.getButton(UnicodeSymbol.U_YES + ResBundle.getMessage("updateProfileState.done"), C_ACCEPT);

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(fullNameButton))
                .keyboardRow(List.of(profileLinkButton))
                .keyboardRow(List.of(jobButton))
                .keyboardRow(List.of(hobbieButton))
                .keyboardRow(List.of(ageButton))
                .keyboardRow(List.of(acceptButton))
                .build();
    }

    @Override
    public void onStart() {
        String response = "Your profile:";

        Message send = ChatWork.sendMessage(chat.getUserId(), response, ParseMode.MARKDOWNV2, getRegIkm());
        chat.setBotMessageId(send.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        onStart();
    }

    @Override
    public void callbackQuery(String data) {
        if (data.equals(C_ACCEPT)) {
            UserModel userModel = new UserDAO().get(chat.getUserId()).get();
            if (userModel.getFullName().equals("")) {
                //toast
                ChatWork.toast(chat, ResBundle.getMessage("updateProfileState.toast"));
            } else {
                Message send = ChatWork.sendMessage(chat.getUserId(), ResBundle.getMessage("updateProfileState.updateProfile"), ParseMode.MARKDOWNV2);
                chat.setBotMessageId(send.getMessageId());
                ChatWork.changeChatState(chat, ChatState.MAIN);
            }

            return;
        }

        ChatState nextState = switch (data) {
            case C_UPDATE_FULL_NAME -> ChatState.UPDATE_FULL_NAME;
            case C_UPDATE_PROFILE_LINK -> ChatState.UPDATE_PROFILE_LINK;
            case C_UPDATE_JOB -> ChatState.UPDATE_JOB;
            case C_UPDATE_HOBBIE -> ChatState.UPDATE_HOBBIE;
            case C_UPDATE_AGE -> ChatState.UPDATE_AGE;
            default -> {
                logger.log(Level.WARNING, "Такой кнопки нет", new RuntimeException("Такой кнопки нет"));
                yield ChatState.UPDATE_PROFILE;
            }
        };

        ChatWork.changeChatState(chat, nextState);
    }
}
