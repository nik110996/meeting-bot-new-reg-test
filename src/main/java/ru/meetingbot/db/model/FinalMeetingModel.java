package ru.meetingbot.db.model;

import ru.meetingbot.db.FinalMeetingState;

import java.time.LocalDate;

public class FinalMeetingModel {

    private long userId;
    private Long userMeetingId;
    private short finalMeetingStateId;
    private Short ratio;
    private LocalDate date;

    public FinalMeetingModel(long userId, short finalMeetingStateId) {
        this.userId = userId;
        this.finalMeetingStateId = finalMeetingStateId;
    }

    public FinalMeetingModel(long userId, Long userMeetingId, short finalMeetingStateId, Short ratio, LocalDate date) {
        this.userId = userId;
        this.userMeetingId = userMeetingId;
        this.finalMeetingStateId = finalMeetingStateId;
        this.ratio = ratio;
        this.date = date;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getUserMeetingId() {
        return userMeetingId;
    }

    public void setUserMeetingId(Long userMeetingId) {
        this.userMeetingId = userMeetingId;
    }

    public short getFinalMeetingStateId() {
        return finalMeetingStateId;
    }

    public void setFinalMeetingStateId(short finalMeetingStateId) {
        this.finalMeetingStateId = finalMeetingStateId;
    }

    public Short getRatio() {
        return ratio;
    }

    public void setRatio(Short ratio) {
        this.ratio = ratio;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "FinalMeetingModel{" +
                " userId=" + userId +
                ", userMeetingId=" + userMeetingId +
                ", finalMeetingStateId=" + FinalMeetingState.stateFromId(finalMeetingStateId).name() +
                ", ratio=" + ratio +
                ", date=" + date +
                '}';
    }
}
