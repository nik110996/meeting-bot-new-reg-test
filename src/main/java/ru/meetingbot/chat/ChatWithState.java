package ru.meetingbot.chat;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.dao.ChatDAO;

public class ChatWithState {

    private final long userId;
    private ChatState state;
    private Update update;
    private int botMessageId;

    public ChatWithState(long userId, ChatState state, int botMessageId) {
        this.userId = userId;
        this.botMessageId = botMessageId;
        setState(state);
    }

    public void writeMessage(Update update) {
        this.update = update;
        state.value().writeMessage(update.getMessage().getText());
    }

    public void callbackQuery(Update update) {
        this.update = update;
        state.value().callbackQuery(update.getCallbackQuery().getData());
    }

    public long getUserId() {
        return userId;
    }
    public ChatState getState() {
        return state;
    }
    public Update getUpdate() {
        return update;
    }
    public int getBotMessageId() {
        return botMessageId;
    }

    public void setState(ChatState state) {
        state.value().setChat(this);
        this.state = state;
        new ChatDAO().update(this);
    }

    public void setBotMessageId(int botMessageId) {
        this.botMessageId = botMessageId;
        new ChatDAO().update(this);
    }

    @Override
    public String toString() {
        return "ChatWithState{" +
                "userId=" + userId +
                ", \n\tstate=" + state.name() +
                ", \n\tbotMessageId=" + botMessageId +
                '}';
    }
}
