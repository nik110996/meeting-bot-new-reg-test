package ru.meetingbot.db.dao;

import ru.meetingbot.db.model.UserStatusModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserStatusDAO extends Dao<UserStatusModel> {

    private Optional<UserStatusModel> getOptionalUserStatusModelFromResultSet(ResultSet resultSet) throws SQLException {
        long userId = resultSet.getLong(T_USER_STATUS_C_USER_ID);
        boolean isFrozen = resultSet.getBoolean(T_USER_STATUS_C_FROZEN);
        boolean isBanned = resultSet.getBoolean(T_USER_STATUS_C_BANNED);

        UserStatusModel userStatusModel = new UserStatusModel(userId, isFrozen, isBanned);

        return Optional.of(userStatusModel);
    }


    public Optional<UserStatusModel> get(long userId) {
        ResultSet resultSet = getAnd(T_USER_STATUS,
                T_USER_STATUS_C_USER_ID, userId);

        try {
            if (resultSet.next()) {
                return getOptionalUserStatusModelFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserStatusModel> get(UserStatusModel userStatusModel) {
        return get(userStatusModel.getUserId());
    }

    @Override
    public List<UserStatusModel> getAll() {
        ResultSet resultSet = getAll(T_USER_STATUS);

        List<UserStatusModel> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<UserStatusModel> optional = getOptionalUserStatusModelFromResultSet(resultSet);

                list.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public int create(UserStatusModel userStatusModel) {
        return create(T_USER_STATUS,
                T_USER_STATUS_C_USER_ID, userStatusModel.getUserId(),
                T_USER_STATUS_C_FROZEN, userStatusModel.isFrozen(),
                T_USER_STATUS_C_BANNED, userStatusModel.isBanned());
    }

    @Override
    public int update(UserStatusModel userStatusModel) {
        return updateByKey(T_USER_STATUS,
                T_USER_STATUS_C_FROZEN, userStatusModel.isFrozen(),
                T_USER_STATUS_C_BANNED, userStatusModel.isBanned(),
                T_USER_STATUS_C_USER_ID, userStatusModel.getUserId());
    }

    @Override
    public int delete(UserStatusModel userStatusModel) {
        return deleteByKey(T_USER_STATUS,
                T_USER_STATUS_C_USER_ID, userStatusModel.getUserId());
    }
}
