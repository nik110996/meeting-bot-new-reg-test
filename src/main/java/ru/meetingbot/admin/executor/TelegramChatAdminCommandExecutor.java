package ru.meetingbot.admin.executor;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.meetingbot.admin.AdminCommand;
import ru.meetingbot.chat.ChatWithState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.FinalMeetingState;
import ru.meetingbot.db.dao.*;
import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.UserModel;
import ru.meetingbot.db.model.UserStatusModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramChatAdminCommandExecutor extends AdminExecutor {

    private static final Logger logger = Logger.getLogger(TelegramChatAdminCommandExecutor.class.getName());

    private long adminId;

    private void sendMessageAllUsers(Message message) {
        List<ChatWithState> users = new ChatDAO().getAll();

        users.removeIf(chatWithState -> {
            boolean remove = false;
            switch (chatWithState.getState()) {
                case START:
                case WRITE_FULL_NAME:
                case WRITE_PROFILE_LINK:
                case WRITE_JOB:
                case WRITE_HOBBIE:
                case WRITE_AGE:
                    remove = true;
            }
            return remove;
        });

        if (message.hasPhoto()) {

            PhotoSize photoSize = message.getPhoto().get(0);
            String fileId = photoSize.getFileId();
            String caption = message.getCaption() != null ? message.getCaption() : "";

            users.forEach(chat -> {
                ChatWork.sendPhotoWithFileId(chat.getUserId(), fileId, caption, ParseMode.MARKDOWNV2);
            });

        } else if (message.hasText()) {
            String text = message.getText();
            users.forEach(chat -> {
                ChatWork.sendMessage(chat.getUserId(), text, ParseMode.MARKDOWNV2);
            });
        }

        ChatWork.sendMessage(adminId, "Сообщение было отправлено всем пользователям");
        ChatWork.changeChatState(adminId, ChatState.MAIN);
    }

    private void banUser(String username) {
        Optional<UserModel> optionalUserModel = new UserDAO().get(username);

        if (optionalUserModel.isPresent()) {
            long id = optionalUserModel.get().getId();

            UserStatusDAO userStatusDAO = new UserStatusDAO();
            UserStatusModel userStatusModel = userStatusDAO.get(id).get();
            userStatusModel.setBanned(true);
            userStatusDAO.update(userStatusModel);

            /* удалить из  таблицы встреч */
//            new MeetingDAO().deleteById(id);

//            ChatDAO chatDAO = new ChatDAO();
//            ChatWithState chatWithState = chatDAO.get(id).get();
//            chatWithState.setState(ChatState.MAIN);
//            chatDAO.update(chatWithState);


            ChatWork.sendMessage(adminId, "Пользователь @" + username + " забанен.");

        } else {
            ChatWork.sendMessage(adminId, "Пользователь @" + username + " не найден.");
        }

        ChatWork.changeChatState(adminId, ChatState.MAIN);
    }

    private void freezeUser(String username) {
        Optional<UserModel> optionalUserModel = new UserDAO().get(username);

        if (optionalUserModel.isPresent()) {
            long id = optionalUserModel.get().getId();

            UserStatusDAO userStatusDAO = new UserStatusDAO();
            UserStatusModel userStatusModel = userStatusDAO.get(id).get();
            userStatusModel.setFrozen(true);
            userStatusDAO.update(userStatusModel);

            ChatWork.sendMessage(adminId, "Пользователь @" + username + " не будет участвовать во встречах.");

        } else {
            ChatWork.sendMessage(adminId, "Пользователь @" + username + " не найден.");
        }

        ChatWork.changeChatState(adminId, ChatState.MAIN);
    }

    private void unfreezeUser(String username) {
        Optional<UserModel> optionalUserModel = new UserDAO().get(username);

        if (optionalUserModel.isPresent()) {
            long id = optionalUserModel.get().getId();

            UserStatusDAO userStatusDAO = new UserStatusDAO();
            UserStatusModel userStatusModel = userStatusDAO.get(id).get();
            userStatusModel.setFrozen(false);
            userStatusDAO.update(userStatusModel);

            ChatWork.sendMessage(adminId, "Пользователь @" + username + " снова участвует во встречах.");

        } else {
            ChatWork.sendMessage(adminId, "Пользователь @" + username + " не найден.");
        }

        ChatWork.changeChatState(adminId, ChatState.MAIN);
    }

    @Override
    protected void ban() {
        ChatWork.sendMessage(adminId, "Введите username пользователя:");
        ChatWork.changeChatState(adminId, ChatState.BAN_USER);
    }

    @Override
    protected void freeze() {
        ChatWork.sendMessage(adminId, "Введите username пользователя:");
        ChatWork.changeChatState(adminId, ChatState.FREEZE_USER);
    }

    @Override
    protected void unfreeze() {
        ChatWork.sendMessage(adminId, "Введите username пользователя:");
        ChatWork.changeChatState(adminId, ChatState.UNFREEZE_USER);
    }

    @Override
    protected void sundayPartOne() {
        super.sundayPartOne();
        ChatWork.sendMessage(adminId, "Воскресенье. Опрос \"Как прошла встреча?\"");
    }

    @Override
    protected void sundayPartTwo() {
        super.sundayPartTwo();
        ChatWork.sendMessage(adminId, "Воскресенье. Опрос \"Кто хочет встретиться на следующей неделе?\"");
    }

    @Override
    protected void monday() {
        super.monday();
        ChatWork.sendMessage(adminId, "Понедельник. Поиск партнёров");
    }

    @Override
    protected void wednesdayPartOne() {
        super.wednesdayPartOne();
        ChatWork.sendMessage(adminId, "Среда. Опрос \"Договорились о встрече?\"");
    }

    @Override
    protected void wednesdayPartTwo() {
        super.wednesdayPartTwo();
        ChatWork.sendMessage(adminId, "Среда. Повторный поиск партнёров");
    }

    @Override
    protected void sendMessage() {
        ChatWork.sendMessage(adminId, "Введите сообщение, которое будет отправленно всем пользователям:");
        ChatWork.changeChatState(adminId, ChatState.SEND_MESSAGE_ALL_USERS);
    }

    @Override
    protected void getCountOfUsers() {
        int countUser = new UserDAO().getCountUser();
        ChatWork.sendMessage(adminId, "Всего пользователей: " + countUser, ParseMode.MARKDOWNV2);
    }

    @Override
    protected void getCountOfUsersOnWeek() {
        int countUserOnWeek = new MeetingDAO().getCountUserOnWeek();
        ChatWork.sendMessage(adminId, "Всего пользователей, участвующих во встречах на этой неделе: " + countUserOnWeek, ParseMode.MARKDOWNV2);
    }

    @Override
    protected void getStatisticAllMeeting() {
        List<FinalMeetingModel> all = new FinalMeetingDAO().getAll();

        FinalMeetingState[] finalMeetingStates = FinalMeetingState.values();
        HashMap<FinalMeetingState, Integer> map = new HashMap<>();
        for (FinalMeetingState finalMeetingState : finalMeetingStates) {
            map.put(finalMeetingState, 0);
        }

        for (FinalMeetingModel finalMeetingModel : all) {
            FinalMeetingState state = FinalMeetingState.stateFromId(finalMeetingModel.getFinalMeetingStateId());
            map.put(state, map.get(state) + 1);
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Статистика по всем встречам:\n\n");
        map.forEach((state, count) -> {
            builder.append(state.value())
                    .append(": ").append(count).append("\n");
        });

        ChatWork.sendMessage(adminId, builder.toString(), ParseMode.MARKDOWNV2);
    }

    @Override
    protected void notCommand() {
        ChatWork.sendMessage(adminId, "_Это не команда админа_", ParseMode.MARKDOWNV2);
    }

    @Override
    protected void help() {
        ChatWork.sendMessage(adminId, getHelp());
    }

    public boolean execute(long adminId, Message message) {
        boolean isAdminCommand = true;
        this.adminId = adminId;
        ChatWithState chatWithState = new ChatDAO().get(adminId).get();

        switch (chatWithState.getState()) {
            case SEND_MESSAGE_ALL_USERS:
                sendMessageAllUsers(message);
                break;
            case BAN_USER:
                if (message.hasText()) {
                    banUser(message.getText());
                }
                break;
            case FREEZE_USER:
                if (message.hasText()) {
                    freezeUser(message.getText());
                }
                break;
            case UNFREEZE_USER:
                if (message.hasText()) {
                    unfreezeUser(message.getText());
                }
                break;
            default:
                String command = message.hasText() ? message.getText() : "notCommand()";
                isAdminCommand = super.execute(command);
                break;
        }

        return isAdminCommand;
    }
}
