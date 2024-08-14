package ru.meetingbot.chat;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.meetingbot.MeetingBot;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.ChatDAO;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ChatWork {

    private static final Logger logger = Logger.getLogger(ChatWork.class.getName());

    private static MeetingBot bot;

    /**
     * Важно установить бота в ChatWork сразу при его создании.
     */
    public static void setMeetingBot(MeetingBot meetingBot) {
        bot = meetingBot;
    }

    /**
     * Проверка текстов по регулярным выражениям
     */
    public static boolean regExp(String text, String regExp, int length) {
        if (text.length() > length) {
            return false;
        }

        return Pattern.matches(regExp, text);
    }

    /**
     * Toast
     */
    public static void toast(ChatWithState chat, String text) {
        String callbackId = chat.getUpdate().getCallbackQuery().getId();
        AnswerCallbackQuery build = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackId)
                .text(text)
                .build();
        try {
            bot.execute(build);
        } catch (TelegramApiException e) {
            logger.log(Level.WARNING, "Telegram API", e);

            throw new RuntimeException(e);
        }
    }

    /**
     * Смена состояний
     */
    public static void changeChatState(long userId, ChatState state) {
        System.out.println(new ChatDAO().get(userId));
        ChatWithState chat = new ChatDAO().get(userId).get();
        changeChatState(chat, state);
    }
    public static void changeChatState(long userId, ChatState state, String command) {
        ChatWithState chat = new ChatDAO().get(userId).get();
        changeChatState(chat, state, command);
    }
    public static void changeChatState(ChatWithState chat, ChatState state) {
        changeChatState(chat, state, null);
    }
    public static void changeChatState(ChatWithState chat, ChatState state, String command) {
        chat.getState().value().onExit();
        chat.setState(state);
        chat.getState().value().onStart();
        if(command != null) {
            chat.getState().value().writeMessage(command);
        }
    }

    /**
     * Удаление сообщений
     *
     */
    public static boolean deleteMessage(Message message) {
        return deleteMessage(message.getChatId(), message.getMessageId());
    }
    public static boolean deleteMessage(Long chatId, int messageId) {
        DeleteMessage delete = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();

        try {
            bot.execute(delete);
        } catch (TelegramApiException e) {
            logger.log(Level.WARNING, "Telegram API", e);
            return false;
        }

        return true;
    }

    /**
     * Отправка сообщений, protection везде отключена!
     */
    public static Message sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, null, null, true, false);
    }
    public static Message sendMessage(Long chatId, String text, boolean protection, boolean notification) {
        return sendMessage(chatId, text, null, null, protection, notification);
    }
    public static Message sendMessage(Long chatId, String text, ReplyKeyboard keyboard) {
        return sendMessage(chatId, text, null, keyboard, true, false);
    }
    public static Message sendMessage(Long chatId, String text, String parseMode) {
        return sendMessage(chatId, text, parseMode, null, true, false);
    }
    public static Message sendMessage(Long chatId, String text, String parseMode, boolean protection, boolean notification) {
        return sendMessage(chatId, text, parseMode, null, protection, notification);
    }
    public static Message sendMessage(Long chatId, String text, String parseMode, ReplyKeyboard keyboard) {
        return sendMessage(chatId, text, parseMode, keyboard, true, false);
    }
    public static Message sendMessage(Long chatId, String text, String parseMode, ReplyKeyboard keyboard, boolean protection, boolean notification) {
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(chatId)
                .text(text);

        if (parseMode != null) {
            messageBuilder.parseMode(parseMode);
        }
        if (keyboard != null) {
            messageBuilder.replyMarkup(keyboard);
        }
        if (protection) {
//            messageBuilder.protectContent();
        }
        if (!notification) {
            messageBuilder.disableNotification(true);
        }
        try {
            return bot.execute(messageBuilder.build());
        } catch (TelegramApiException e) {
            Message message = new Message();
            message.setMessageId(-1);
            logger.log(Level.WARNING, "Telegram API", e);
            return message;
        }
    }

    /**
     * Отправка сообщения с картинкой.
     * Путь к картинке, начинается с "pics"
     * pathPic = Path.of("pics", ..., "name.jpg");
     */
    public static Message sendPhoto(Long chatId, String pic, String caption, ReplyKeyboard keyboard) {
        return sendPhoto(chatId, pic, caption, null, keyboard, true, false);
    }
    public static Message sendPhoto(Long chatId, String pic, String caption, String parseMode) {
        return sendPhoto(chatId, pic, caption, parseMode, null, true, false);
    }
    public static Message sendPhoto(Long chatId, String pic, String caption, String parseMode, ReplyKeyboard keyboard) {
        return sendPhoto(chatId, pic, caption, parseMode, keyboard, true, false);
    }
    public static Message sendPhoto(Long chatId, String pic, String caption, String parseMode, ReplyKeyboard keyboard, boolean protection, boolean notification) {
        String[] split = pic.split("[\\\\/]");
        String name = split[split.length-1];
        SendPhoto.SendPhotoBuilder builder = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(ChatWork.class.getClassLoader().getResourceAsStream(pic), name))
                .caption(caption);

        if (parseMode != null) {
            builder.parseMode(parseMode);
        }
        if (keyboard != null) {
            builder.replyMarkup(keyboard);
        }
        if (protection) {
//            builder.protectContent(true);
        }
        if (!notification) {
            builder.disableNotification(true);
        }

        try {
            return bot.execute(builder.build());
        } catch (TelegramApiException e) {
            Message message = new Message();
            message.setMessageId(-1);
            logger.log(Level.WARNING, "Telegram API", e);
            return message;
        }
    }

    /**
     * Отправка картинки с fileId.
     */
    public static Message sendPhotoWithFileId(Long chatId, String fileId, String caption, String parseMode) {
        SendPhoto.SendPhotoBuilder builder = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(fileId))
                .caption(caption)
                .parseMode(parseMode);

        try {
            return bot.execute(builder.build());
        } catch (TelegramApiException e) {
            Message message = new Message();
            message.setMessageId(-1);
            logger.log(Level.WARNING, "Telegram API", e);
            return message;
        }
    }

    /**
     * Получить готовую кнопку
     */
    public static InlineKeyboardButton getButton(String text, String data) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(data)
                .build();
    }

    public static MeetingBot getBot() {
        return bot;
    }
}
