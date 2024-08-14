package ru.meetingbot.db.dao;

import ru.meetingbot.db.model.MeetingModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MeetingDAO extends Dao<MeetingModel> {

    private static final Logger logger = Logger.getLogger(MeetingDAO.class.getName());


    private Optional<MeetingModel> getOptionalMeetingModelFromResultSet(ResultSet resultSet) throws SQLException {
        long userId = resultSet.getLong(T_MEETING_C_USER_ID);
        short meetingStateId = resultSet.getShort(T_MEETING_C_MEETING_STATE_ID);
        Long userMeetingId = resultSet.getLong(T_MEETING_C_USER_MEETING_ID);
        if (userMeetingId == 0) {
            userMeetingId = null;
        }
        MeetingModel meetingModel = new MeetingModel(userId, meetingStateId, userMeetingId);
        return Optional.of(meetingModel);
    }

    public int getCountUserOnWeek() {
        String sql = "SELECT count(*) from " + T_MEETING;
        ResultSet resultSet = executeQuery(sql);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQL", e);
            return 0;
        }
    }

    public Optional<MeetingModel> get(long userId) {
        ResultSet resultSet = getAnd(T_MEETING,
                T_MEETING_C_USER_ID, userId);

        try {
            if (resultSet.next()) {
                return getOptionalMeetingModelFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public List<MeetingModel> getWhere(boolean and, Object... objects) {
        ResultSet resultSet;

        if (and) {
            resultSet = getAnd(T_MEETING, objects);
        } else {
            resultSet = getOr(T_MEETING, objects);
        }

        List<MeetingModel> meetingModels = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<MeetingModel> optional = getOptionalMeetingModelFromResultSet(resultSet);

                meetingModels.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return meetingModels;
    }

    @Override
    public Optional<MeetingModel> get(MeetingModel meetingModel) {
        return get(meetingModel.getUserId());
    }

    @Override
    public List<MeetingModel> getAll() {
        ResultSet resultSet = getAll(T_MEETING);

        List<MeetingModel> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<MeetingModel> optional = getOptionalMeetingModelFromResultSet(resultSet);

                list.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public int create(MeetingModel meetingModel) {
        return create(T_MEETING,
                T_MEETING_C_USER_ID, meetingModel.getUserId(),
                T_MEETING_C_MEETING_STATE_ID, meetingModel.getMeetingStateId(),
                T_MEETING_C_USER_MEETING_ID, meetingModel.getUserMeetingId());
    }

    @Override
    public int update(MeetingModel meetingModel) {
        return updateByKey(T_MEETING,
                T_MEETING_C_MEETING_STATE_ID, meetingModel.getMeetingStateId(),
                T_MEETING_C_USER_MEETING_ID, meetingModel.getUserMeetingId(),
                T_MEETING_C_USER_ID, meetingModel.getUserId());
    }

    @Override
    public int delete(MeetingModel meetingModel) {
        return deleteById(meetingModel.getUserId());
    }

    public int deleteById(long userId) {
        return deleteByKey(T_MEETING,
                T_MEETING_C_USER_ID, userId);
    }

    public int deleteWhere(boolean and, Object... objects) {
        if (and) {
            return deleteAnd(T_MEETING, objects);
        } else {
            return deleteOr(T_MEETING, objects);
        }
    }

}
