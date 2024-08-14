package ru.meetingbot.db.dao;

import ru.meetingbot.db.model.FinalMeetingModel;
import ru.meetingbot.db.model.MeetingModel;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class FinalMeetingDAO extends Dao<FinalMeetingModel> {

    private Optional<FinalMeetingModel> getOptionalFinalMeetingModelFromResultSet(ResultSet resultSet) throws SQLException {
        long oneId = resultSet.getLong(T_FINAL_MEETING_C_USER_ID);
        Long twoId = resultSet.getLong(T_FINAL_MEETING_C_USER_MEETING_ID);
        short finalMeetingStateId = resultSet.getShort(T_FINAL_MEETING_C_FINAL_MEETING_STATE_ID);
        Short ratio = resultSet.getShort(T_FINAL_MEETING_C_RATIO);
        LocalDate date = resultSet.getDate(T_FINAL_MEETING_C_DATE_MEETING).toLocalDate();

        if (twoId == 0) {
            twoId = null;
        }

        if (ratio == 0) {
            ratio = null;
        }

        FinalMeetingModel finalMeetingModel = new FinalMeetingModel(oneId, twoId, finalMeetingStateId, ratio, date);
        return Optional.of(finalMeetingModel);
    }

    public Optional<FinalMeetingModel> get(long userId) {
        ResultSet resultSet = getAnd(T_FINAL_MEETING,
                T_FINAL_MEETING_C_USER_ID, userId);

        try {
            if (resultSet.next()) {
                return getOptionalFinalMeetingModelFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    public List<FinalMeetingModel> getWhere(boolean and, Object... objects) {
        ResultSet resultSet;

        if (and) {
            resultSet = getAnd(T_FINAL_MEETING, objects);
        } else {
            resultSet = getOr(T_FINAL_MEETING, objects);
        }

        List<FinalMeetingModel> meetingModels = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<FinalMeetingModel> optional = getOptionalFinalMeetingModelFromResultSet(resultSet);

                meetingModels.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return meetingModels;
    }

    @Override
    public Optional<FinalMeetingModel> get(FinalMeetingModel finalMeetingModel) {
        return get(finalMeetingModel.getUserId());
    }

    @Override
    public List<FinalMeetingModel> getAll() {
        ResultSet resultSet = getAll(T_FINAL_MEETING);

        List<FinalMeetingModel> list = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Optional<FinalMeetingModel> optional = getOptionalFinalMeetingModelFromResultSet(resultSet);

                list.add(optional.get());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public int create(FinalMeetingModel finalMeetingModel) {

        return create(T_FINAL_MEETING,
                T_FINAL_MEETING_C_USER_ID, finalMeetingModel.getUserId(),
                T_FINAL_MEETING_C_USER_MEETING_ID, finalMeetingModel.getUserMeetingId(),
                T_FINAL_MEETING_C_FINAL_MEETING_STATE_ID, finalMeetingModel.getFinalMeetingStateId(),
                T_FINAL_MEETING_C_RATIO, finalMeetingModel.getRatio(),
                T_FINAL_MEETING_C_DATE_MEETING, finalMeetingModel.getDate());
    }

    /**
     * обновляется WHERE user_id=? AND user_meeting_id=? AND date_meeting=?
     */
    @Override
    public int update(FinalMeetingModel finalMeetingModel) {

        return updateWhere(T_FINAL_MEETING, 2,
                T_FINAL_MEETING_C_FINAL_MEETING_STATE_ID, finalMeetingModel.getFinalMeetingStateId(),
                T_FINAL_MEETING_C_RATIO, finalMeetingModel.getRatio(),
                T_FINAL_MEETING_C_USER_MEETING_ID, finalMeetingModel.getUserMeetingId(),
                T_FINAL_MEETING_C_DATE_MEETING, Date.valueOf(finalMeetingModel.getDate()),
                T_FINAL_MEETING_C_USER_ID, finalMeetingModel.getUserId());
    }

    @Override
    public int delete(FinalMeetingModel finalMeetingModel) {

        return deleteByKey(T_FINAL_MEETING,
                T_FINAL_MEETING_C_USER_ID, finalMeetingModel.getUserId());
    }

    public int deleteWhere(boolean and, Object... objects) {
        if (and) {
            return deleteAnd(T_FINAL_MEETING, objects);
        } else {
            return deleteOr(T_FINAL_MEETING, objects);
        }
    }

    public Optional<FinalMeetingModel> getLastMeetingBetweenUsers(long userId1, long userId2) {
        String sql = "SELECT * FROM " + T_FINAL_MEETING + " WHERE " +
                "((" + T_FINAL_MEETING_C_USER_ID + " = ? AND " + T_FINAL_MEETING_C_USER_MEETING_ID + " = ?) OR " +
                "(" + T_FINAL_MEETING_C_USER_ID + " = ? AND " + T_FINAL_MEETING_C_USER_MEETING_ID + " = ?)) " +
                "AND " + T_FINAL_MEETING_C_FINAL_MEETING_STATE_ID + " BETWEEN 3 AND 6 " +
                "ORDER BY " + T_FINAL_MEETING_C_DATE_MEETING + " DESC LIMIT 1";

        try (ResultSet resultSet = executeQuery(sql,
                T_FINAL_MEETING_C_USER_ID, userId1,
                T_FINAL_MEETING_C_USER_MEETING_ID, userId2,
                T_FINAL_MEETING_C_USER_ID, userId2,
                T_FINAL_MEETING_C_USER_MEETING_ID, userId1)) {

            if (resultSet.next()) {
                return getOptionalFinalMeetingModelFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            //logger.log(Level.WARNING, "SQL Exception in getLastMeetingBetweenUsers", e);
        }

        return Optional.empty();
    }

}
