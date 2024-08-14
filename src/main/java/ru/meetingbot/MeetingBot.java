package ru.meetingbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.meetingbot.admin.executor.TelegramChatAdminCommandExecutor;
import ru.meetingbot.chat.ChatWithState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.ChatDAO;
import ru.meetingbot.db.dao.UserStatusDAO;
import ru.meetingbot.db.model.UserStatusModel;
import ru.meetingbot.util.PropertiesUtils;
import ru.meetingbot.util.UsersUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MeetingBot extends TelegramLongPollingBot {
    private final String BOT_NAME;

    private final String BOT_TOKEN;

    private final TelegramChatAdminCommandExecutor adminCommandExecutor;
    private final List<Long> admins;

    /**
     * Получение id админов из файла bot.properties
     * В файле можно добавлять через запятую id админов
     */
    private List<Long> setupAdmins() {
        String[] strings = PropertiesUtils.getProperties("bot").getProperty("admin").split(",");

        return Arrays.stream(strings).map(Long::parseLong).toList();
    }

    private boolean isAdmin(long id) {
        for (Long admin : admins) {
            if (admin == id) {
                return true;
            }
        }

        return false;
    }

    public MeetingBot() {
        Properties properties = PropertiesUtils.getProperties("bot");
        BOT_NAME = properties.getProperty("name");
        BOT_TOKEN = properties.getProperty("token");

        adminCommandExecutor = new TelegramChatAdminCommandExecutor();
        admins = setupAdmins();

        ChatWork.setMeetingBot(this);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();

            if (UsersUtils.checkValidUser(message)) {
                return;
            }

            /* если это админ */
            if (isAdmin(chatId)) {
                boolean isAdminCommand = adminCommandExecutor.execute(chatId, message);
                /* если выполнилась команда админа, то не идти дальше */
                if (isAdminCommand) {
                    return;
                }
            }

            /* если забанен */
            if (UsersUtils.isBanned(chatId)) {
                ChatWork.sendMessage(chatId, ResBundle.getMessage("meetingBot.ifBanned"), ParseMode.MARKDOWNV2);
                return;
            }

            /* если был заморожен */
            if (UsersUtils.isFrozen(chatId)) {
                UserStatusDAO userStatusDAO = new UserStatusDAO();
                UserStatusModel userStatusModel = userStatusDAO.get(chatId).get();
                userStatusModel.setFrozen(false);
                userStatusDAO.update(userStatusModel);
            }

            /* Если нет текста в сообщении, то удалить это сообщение */
            if (!message.hasText()) {
                ChatWork.deleteMessage(message);
                return;
            }

            /* При команде /start (если удалил чат и заново зашёл) отбрасывает на заполнение анкеты*/
            if (message.getText().equals("/start")) {
                ChatWork.changeChatState(chatId, ChatState.START);
                return;
            }

            ChatWithState chatWithState = new ChatDAO().get(chatId).get();
            chatWithState.writeMessage(update);

        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();

            if (UsersUtils.checkValidUser(message)) {
                return;
            }

            ChatWithState chatWithState = new ChatDAO().get(message.getChatId()).get();

            /* если кнопка нажата с другой клавиатуры, то не выполнять её*/
            if (chatWithState.getBotMessageId() != message.getMessageId()) {
                return;
            }

            chatWithState.callbackQuery(update);
        }
    }

}
