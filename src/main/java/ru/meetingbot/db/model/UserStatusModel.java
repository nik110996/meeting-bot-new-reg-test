package ru.meetingbot.db.model;

public class UserStatusModel {

    private long userId;
    private boolean isFrozen;
    private boolean isBanned;

    public UserStatusModel(long userId, boolean isFrozen, boolean isBanned) {
        this.userId = userId;
        this.isFrozen = isFrozen;
        this.isBanned = isBanned;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    @Override
    public String toString() {
        return "UserStatusModel{" +
                " userId=" + userId +
                ", isFrozen=" + isFrozen +
                ", isBanned=" + isBanned +
                '}';
    }
}
