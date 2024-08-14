package ru.meetingbot.db;

public enum MeetingState {
    /* monday */
    M_HAVE_PARTNER(1, "есть пара в понедельник"),
    M_NOT_HAVE_PARTNER(2, "нет пары в понедельник"),

    /* wednesday */
    W_ALREADY_AGREED(3, "договорились до среды"),
    W_TRYING_TO_NEGOTIATE(4, "ещё попробуют договориться"),
    W_DID_NOT_ANSWER(5, "не ответил в среду"),
    W_NOT_HAVE_PARTNER(6, "нет пары в среду"),
    W_HAVE_PARTNER(7, "есть пара в среду"),

    /* sunday */
    S_ACCEPT_MEETING(8, "в воскресенье согласился на встречу");

    private final short meetingStateId;
    private final String value;

    MeetingState(int meetingStateId, String value) {
        this.meetingStateId = (short) meetingStateId;
        this.value = value;
    }

    public static MeetingState stateFromId(int meetingStateId) {
        for (MeetingState ms : MeetingState.values()) {
            if (ms.id() == meetingStateId) {
                return ms;
            }
        }
        throw new RuntimeException();
    }

    public short id() {
        return meetingStateId;
    }

    public String value() {
        return value;
    }
}
