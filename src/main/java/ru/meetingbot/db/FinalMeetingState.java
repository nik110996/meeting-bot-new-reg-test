package ru.meetingbot.db;

public enum FinalMeetingState {

    M_SKIP_WEEK(1, "пропустил неделю"),

    W_NOT_HAVE_PARTNER(2, "нет пары в среду"),

    MEET_W_ALREADY_AGREED(3, "договорился до среды и встретился"),
    MEET_W_TRYING_TO_NEGOTIATE(4, "договорился после среды и встретился"),
    MEET_W_DID_NOT_ANSWER(5, "не ответил в среду и встретился"),
    MEET_W_HAVE_PARTNER(6, "нашёл пару в среду и встретился"),

    NOT_MEET_W_ALREADY_AGREED(7, "договорился до среды и НЕ встретился"),
    NOT_MEET_W_TRYING_TO_NEGOTIATE(8, "договорился после среды и НЕ встретился"),
    NOT_MEET_W_DID_NOT_ANSWER(9, "не ответил в среду и НЕ встретился"),
    NOT_MEET_W_HAVE_PARTNER(10, "нашёл пару в среду и НЕ встретился"),

    NOT_MEET(11, "партнёр сказал, что встречи не было"),

    NOT_ANSWER_W_ALREADY_AGREED(12, "договорился до среды и не ответил была ли встреча"),
    NOT_ANSWER_W_TRYING_TO_NEGOTIATE(13, "договорился после среды и не ответил была ли встреча"),
    NOT_ANSWER_W_DID_NOT_ANSWER(14, "не ответил в среду и не ответил была ли встреча"),
    NOT_ANSWER_W_HAVE_PARTNER(15, "нашёл пару в среду и не ответил была ли встреча"),

    REFUSED_TO_MEET(16, "отказался от встречи на этой неделе"),

    M_FINAL_NOT_HAVE_PARTNER(17, "нет пары с понедельника");


    private final short finalMeetingStateId;
    private final String value;

    FinalMeetingState(int finalMeetingStateId, String value) {
        this.finalMeetingStateId = (short) finalMeetingStateId;
        this.value = value;
    }

    public static boolean wasMeeting(FinalMeetingState state) {
        return switch (state) {
            case MEET_W_ALREADY_AGREED,
                    MEET_W_TRYING_TO_NEGOTIATE,
                    MEET_W_DID_NOT_ANSWER,
                    MEET_W_HAVE_PARTNER -> true;
            default -> false;
        };
    }

    public static boolean wasNotMeeting(FinalMeetingState state) {
        return switch (state) {
            case NOT_MEET_W_ALREADY_AGREED,
                    NOT_MEET_W_TRYING_TO_NEGOTIATE,
                    NOT_MEET_W_DID_NOT_ANSWER,
                    NOT_MEET_W_HAVE_PARTNER,
                    NOT_MEET -> true;
            default -> false;
        };
    }

    public static boolean wasNotAnswer(FinalMeetingState state) {
        return switch (state) {
            case NOT_ANSWER_W_ALREADY_AGREED,
                    NOT_ANSWER_W_TRYING_TO_NEGOTIATE,
                    NOT_ANSWER_W_DID_NOT_ANSWER,
                    NOT_ANSWER_W_HAVE_PARTNER -> true;
            default -> false;
        };
    }

    /**
     * -1 -> не было встречи
     *  0 -> не ответил
     *  1 -> да, встреча была
     */
    public static FinalMeetingState stateFromMeetingState(int flagMeet, MeetingState meetingState) {
        if (flagMeet == 1) {
            return switch (meetingState) {
                case W_DID_NOT_ANSWER -> MEET_W_DID_NOT_ANSWER;
                case W_HAVE_PARTNER -> MEET_W_HAVE_PARTNER;
                case W_ALREADY_AGREED -> MEET_W_ALREADY_AGREED;
                case W_TRYING_TO_NEGOTIATE -> MEET_W_TRYING_TO_NEGOTIATE;
                default -> throw new IllegalStateException("Unexpected value: " + meetingState);
            };
        } else if (flagMeet == -1) {
            return switch (meetingState) {
                case W_DID_NOT_ANSWER -> NOT_MEET_W_DID_NOT_ANSWER;
                case W_HAVE_PARTNER -> NOT_MEET_W_HAVE_PARTNER;
                case W_ALREADY_AGREED -> NOT_MEET_W_ALREADY_AGREED;
                case W_TRYING_TO_NEGOTIATE -> NOT_MEET_W_TRYING_TO_NEGOTIATE;
                default -> throw new IllegalStateException("Unexpected value: " + meetingState);
            };
        } else if (flagMeet == 0 ) {
            return switch (meetingState) {
                case W_DID_NOT_ANSWER -> NOT_ANSWER_W_DID_NOT_ANSWER;
                case W_HAVE_PARTNER -> NOT_ANSWER_W_HAVE_PARTNER;
                case W_ALREADY_AGREED -> NOT_ANSWER_W_ALREADY_AGREED;
                case W_TRYING_TO_NEGOTIATE -> NOT_ANSWER_W_TRYING_TO_NEGOTIATE;
                /* ситуация, когда согласился участвовать в понедельник */
                case M_NOT_HAVE_PARTNER -> M_FINAL_NOT_HAVE_PARTNER;
                default -> throw new IllegalStateException("Unexpected value: " + meetingState);
            };
        } else {
            throw new RuntimeException("Может быть только -1, 0, 1");
        }
    }

    public static FinalMeetingState stateFromId(int finalMeetingStateId) {
        for (FinalMeetingState fms : FinalMeetingState.values()) {
            if (fms.id() == finalMeetingStateId) {
                return fms;
            }
        }
        throw new RuntimeException();
    }

    public short id() {
        return finalMeetingStateId;
    }

    public String value() {
        return value;
    }
}
