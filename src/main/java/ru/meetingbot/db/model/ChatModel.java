package ru.meetingbot.db.model;

/**
 * Вместо этого класса используется ChatWithState
 */
public class ChatModel {

    protected long userId;
    protected short chatStateId;
    protected int botMessageId;

    public ChatModel(long userId, short chatStateId) {
        this.userId = userId;
        this.chatStateId = chatStateId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public short getChatStateId() {
        return chatStateId;
    }

    public void setChatStateId(short chatStateId) {
        this.chatStateId = chatStateId;
    }

    public int getBotMessageId() {
        return botMessageId;
    }

    public void setBotMessageId(int botMessageId) {
        this.botMessageId = botMessageId;
    }

}
