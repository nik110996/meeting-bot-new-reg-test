package ru.meetingbot.chat.state;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.meetingbot.ResBundle;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.chat.consts.Command;

import java.util.List;

/**
 * Это состояние отключёно, после кнопки START в боте, начинается ввод имени: ChatState.WRITE_FULL_NAME
 */
public class StartState extends BaseChatState {

//    private InlineKeyboardMarkup getStartIkm() {
//        var regButton = ChatWork.getButton(ResBundle.getMessage("reg.button"), Command.C_WRITE_FULL_NAME);
//
//        return InlineKeyboardMarkup.builder()
//                .keyboardRow(List.of(regButton))
//                .build();
//    }

    @Override
    public void onStart() {
        ChatWork.changeChatState(chat, ChatState.WRITE_FULL_NAME);

//        String response = ResBundle.getMessage("reg.text");
//
//        Message send = ChatWork.sendMessage(chat.getUserId(), response, ParseMode.MARKDOWNV2, getStartIkm());
//        chat.setBotMessageId(send.getMessageId());
    }

    @Override
    public void writeMessage(String message) {
        onStart();
    }

    @Override
    public void callbackQuery(String data) {
        switch (data) {
            case C_WRITE_FULL_NAME -> ChatWork.changeChatState(chat, ChatState.WRITE_FULL_NAME);
            default -> onStart();
        }
    }
}
