package ru.meetingbot.db.model;

import ru.meetingbot.db.MeetingState;

public class MeetingModel {

    private long userId;
    private short meetingStateId;
    private Long userMeetingId;

    public MeetingModel(long userId, short meetingStateId, Long userMeetingId) {
        this.userId = userId;
        this.meetingStateId = meetingStateId;
        this.userMeetingId = userMeetingId;
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

    public short getMeetingStateId() {
        return meetingStateId;
    }

    public void setMeetingStateId(short meetingStateId) {
        this.meetingStateId = meetingStateId;
    }

    @Override
    public String toString() {
        return "MeetingModel{" +
                " userId=" + userId +
                ", meetingStateId=" + MeetingState.stateFromId(meetingStateId).name() +
                ", userMeetingId=" + userMeetingId +
                '}';
    }
}
