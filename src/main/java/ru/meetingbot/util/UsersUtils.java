package ru.meetingbot.util;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.meetingbot.ResBundle;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.dao.UserDAO;
import ru.meetingbot.db.dao.UserStatusDAO;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.db.model.UserStatusModel;

import java.util.Optional;

public class UsersUtils {

    /* Если username не установлен */
    public static boolean isUsernameEmpty(String userName) {
        return userName == null || userName.equals("");
    }
    /* Если пользователь зашёл первый раз */
    public static boolean isFirstTime(Optional<UserModel> optionalUserModel) {
        return optionalUserModel.isEmpty();
    }

    public static boolean checkValidUser(Message message) {
        Chat chat = message.getChat();
        Long chatId = message.getChatId();
        String userName = chat.getUserName();

        /* Если username не установлен */
        if (isUsernameEmpty(userName)) {
            ChatWork.sendMessage(chatId, ResBundle.getMessage("meetingBot.ifUsernameIsEmpty"), ParseMode.MARKDOWNV2);
            return true;
        }

        UserDAO userDAO = new UserDAO();
        Optional<UserModel> optionalUserModel = userDAO.get(chatId);

        /* Если пользователь зашёл первый раз */
        if (UsersUtils.isFirstTime(optionalUserModel)) {
            UserModel userModel = new UserModel(chatId, userName);
            userDAO.create(userModel);
            return false;
        }

        /* Если изменился username */
        UserModel userModel = optionalUserModel.get();
        if (! userName.equals(userModel.getUserName())) {
            userModel.setUserName(userName);
            userDAO.update(userModel);
        }

        return false;
    }

    public static boolean isBanned(long userId) {
        Optional<UserStatusModel> userStatusModel = new UserStatusDAO().get(userId);
        if (userStatusModel.isEmpty()) {
            return false;
        }

        return userStatusModel.get().isBanned();
    }

    public static boolean isFrozen(long userId) {
        Optional<UserStatusModel> userStatusModel = new UserStatusDAO().get(userId);
        if (userStatusModel.isEmpty()) {
            return false;
        }

        return userStatusModel.get().isFrozen();
    }
}
