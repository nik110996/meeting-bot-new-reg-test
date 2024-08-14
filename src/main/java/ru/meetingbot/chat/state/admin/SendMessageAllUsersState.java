package ru.meetingbot.chat.state.admin;

import ru.meetingbot.chat.state.BaseChatState;

/**
 * остаётся пустым, вся обработка сообщений в этом состоянии происходит в TelegramChatAdminCommandExecutor
 */
public class SendMessageAllUsersState extends BaseChatState {

    @Override
    public void writeMessage(String message) {}

    @Override
    public void callbackQuery(String data) {}
}
