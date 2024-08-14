package ru.meetingbot.chat.state;

import ru.meetingbot.chat.ChatWithState;
import ru.meetingbot.chat.consts.Command;
import ru.meetingbot.chat.consts.RegExp;
import ru.meetingbot.chat.consts.UnicodeSymbol;

public abstract class BaseChatState implements UnicodeSymbol, Command, RegExp {

    protected ChatWithState chat;

    public void onStart() {}

    public abstract void writeMessage(String message);

    public abstract void callbackQuery(String data);

    public void onExit() {}

    public void setChat(ChatWithState chat) {
        this.chat = chat;
    }

}
