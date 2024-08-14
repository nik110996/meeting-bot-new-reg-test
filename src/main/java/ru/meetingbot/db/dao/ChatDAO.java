package ru.meetingbot.db.dao;

import ru.meetingbot.chat.ChatWithState;
import ru.meetingbot.db.ChatState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatDAO extends Dao<ChatWithState> {

    private Optional<ChatWithState> getOptionalChatWithStateFromResultSet(ResultSet resultSet) throws SQLException {
        long userId = resultSet.getLong(T_CHAT_C_USER_ID);
        short chatStateId = resultSet.getShort(T_CHAT_C_CHAT_STATE_ID);
        int botMessageId = resultSet.getInt(T_CHAT_C_BOT_MESSAGE_ID);

        ChatState state = ChatState.stateFromId(chatStateId);
        ChatWithState chat = new ChatWithState(userId, state, botMessageId);

        return Optional.of(chat);
    }

    public Optional<ChatWithState> get(long userId) {
        ResultSet resultSet = getAnd(T_CHAT,
                T_CHAT_C_USER_ID, userId);

        try {
            if (resultSet.next()) {
                return getOptionalChatWithStateFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public List<ChatWithState> getWhere(boolean and, Object... objects) {
        ResultSet resultSet;

        if (and) {
            resultSet = getAnd(T_CHAT, objects);
        } else {
            resultSet = getOr(T_CHAT, objects);
        }

        List<ChatWithState> chatWithStates = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<ChatWithState> optional = getOptionalChatWithStateFromResultSet(resultSet);

                chatWithStates.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return chatWithStates;
    }

    @Override
    public Optional<ChatWithState> get(ChatWithState chatWithState) {
        return get(chatWithState.getUserId());
    }

    @Override
    public List<ChatWithState> getAll() {
        ResultSet resultSet = getAll(T_CHAT);

        List<ChatWithState> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<ChatWithState> optional = getOptionalChatWithStateFromResultSet(resultSet);

                list.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public int create(ChatWithState chat) {
        return create(T_CHAT,
                T_CHAT_C_USER_ID, chat.getUserId(),
                T_CHAT_C_CHAT_STATE_ID, chat.getState().id(),
                T_CHAT_C_BOT_MESSAGE_ID, chat.getBotMessageId());
    }

    @Override
    public int update(ChatWithState chat) {
        return updateByKey(T_CHAT,
                T_CHAT_C_CHAT_STATE_ID, chat.getState().id(),
                T_CHAT_C_BOT_MESSAGE_ID, chat.getBotMessageId(),
                T_CHAT_C_USER_ID, chat.getUserId());
    }

    @Override
    public int delete(ChatWithState chat) {
        return deleteByKey(T_CHAT,
                T_CHAT_C_USER_ID, chat.getUserId());
    }
}
