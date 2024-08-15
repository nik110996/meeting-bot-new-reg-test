package ru.meetingbot.db.dao;

import ru.meetingbot.db.ChatState;
import ru.meetingbot.db.connection.ConnectionFactory;
import ru.meetingbot.db.model.UserModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends Dao<UserModel> {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());
    
    private Optional<UserModel> getOptionalUserModelFromResultSet(ResultSet resultSet) throws SQLException {
            long id_user = resultSet.getLong(T_USER_C_ID);
            String userName = resultSet.getString(T_USER_C_USER_NAME);
            String fullName = resultSet.getString(T_USER_C_FULL_NAME);
            String profileLink = resultSet.getString(T_USER_C_PROFILE_LINK);
            String job = resultSet.getString(T_USER_C_JOB);
            String hobbie = resultSet.getString(T_USER_C_HOBBIE);
            short yearsOfExperience = resultSet.getShort(T_USER_C_YEARS_OF_EXPERIENCE);
            String location = resultSet.getString(T_USER_C_YEARS_OF_LOCATION);

            UserModel userModel = new UserModel(id_user, userName, fullName, profileLink, job, hobbie, yearsOfExperience, location);
            return Optional.of(userModel);
    }

    public int getCountUser() {
        String sql = "SELECT count(*) from " + T_USER;
        ResultSet resultSet = executeQuery(sql);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQL", e);
            return 0;
        }
    }

    public Optional<UserModel> get(String username) {
        ResultSet resultSet = getAnd(T_USER, T_USER_C_USER_NAME, username);

        try {
            if(resultSet.next()) {
                return getOptionalUserModelFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public Optional<UserModel> get(long id) {
        ResultSet resultSet = getAnd(T_USER,
                T_USER_C_ID, id);

        try {
            if(resultSet.next()) {
                return getOptionalUserModelFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserModel> get(UserModel userModel) {
        return get(userModel.getId());
    }

    @Override
    public List<UserModel> getAll() {
        ResultSet resultSet = getAll(T_USER);

        List<UserModel> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<UserModel> optional = getOptionalUserModelFromResultSet(resultSet);

                list.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public int create(UserModel userModel) {
        Connection conn = null;

        try (Connection connection = ConnectionFactory.getConnection()) {
            conn = connection;
            connection.setAutoCommit(false);

            int affectedRows = create(connection, T_USER,
                    T_USER_C_ID, userModel.getId(),
                    T_USER_C_USER_NAME, userModel.getUserName(),
                    T_USER_C_FULL_NAME, userModel.getFullName(),
                    T_USER_C_PROFILE_LINK, userModel.getProfileLink(),
                    T_USER_C_JOB, userModel.getJob(),
                    T_USER_C_HOBBIE, userModel.getHobbie(),
                    T_USER_C_YEARS_OF_EXPERIENCE, userModel.getYearsOfExperience(),
                    T_USER_C_YEARS_OF_LOCATION, userModel.getLocation());

            if (affectedRows > 0) {
                affectedRows = create(connection, T_CHAT,
                        T_CHAT_C_USER_ID, userModel.getId(),
                        T_CHAT_C_CHAT_STATE_ID, ChatState.START.id());
            } else {
                connection.rollback();
            }

            if (affectedRows > 0) {
                create(connection, T_USER_STATUS,
                        T_USER_STATUS_C_USER_ID, userModel.getId(),
                        T_USER_STATUS_C_FROZEN, false,
                        T_USER_STATUS_C_BANNED, false);
            } else {
                connection.rollback();
            }

            connection.commit();
            return affectedRows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(UserModel userModel) {
        return updateByKey(T_USER,
                T_USER_C_USER_NAME, userModel.getUserName(),
                T_USER_C_FULL_NAME, userModel.getFullName(),
                T_USER_C_PROFILE_LINK, userModel.getProfileLink(),
                T_USER_C_JOB, userModel.getJob(),
                T_USER_C_HOBBIE, userModel.getHobbie(),
                T_USER_C_YEARS_OF_EXPERIENCE, userModel.getYearsOfExperience(),
                T_USER_C_YEARS_OF_LOCATION, userModel.getLocation(),
                T_USER_C_ID, userModel.getId());
    }

    @Override
    public int delete(UserModel userModel) {
        return deleteByKey(T_USER,
                T_USER_C_ID, userModel.getId());
    }
}
