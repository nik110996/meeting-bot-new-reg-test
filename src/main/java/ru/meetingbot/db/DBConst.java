package ru.meetingbot.db;

public interface DBConst {

    String T_CHAT = "public.chat";
    String T_CHAT_C_USER_ID = "user_id";
    String T_CHAT_C_CHAT_STATE_ID = "chat_state_id";
    String T_CHAT_C_BOT_MESSAGE_ID = "bot_message_id";

    String T_USER = "public.user";
    String T_USER_C_ID = "id";
    String T_USER_C_USER_NAME = "user_name";
    String T_USER_C_FULL_NAME = "full_name";
    String T_USER_C_PROFILE_LINK = "profile_link";
    String T_USER_C_JOB = "job";
    String T_USER_C_HOBBIE = "hobbie";
    String T_USER_C_YEARS_OF_EXPERIENCE = "years_of_experience";
    String T_USER_C_YEARS_OF_LOCATION = "location";

    String T_MEETING = "public.meeting";
    String T_MEETING_C_USER_ID = "user_id";
    String T_MEETING_C_MEETING_STATE_ID = "meeting_state_id";
    String T_MEETING_C_USER_MEETING_ID = "user_meeting_id";

    String T_FINAL_MEETING = "public.final_meeting";
    String T_FINAL_MEETING_C_USER_ID = "user_id";
    String T_FINAL_MEETING_C_USER_MEETING_ID = "user_meeting_id";
    String T_FINAL_MEETING_C_FINAL_MEETING_STATE_ID = "final_meeting_state_id";
    String T_FINAL_MEETING_C_RATIO = "ratio";
    String T_FINAL_MEETING_C_DATE_MEETING = "date_meeting";

    String T_USER_STATUS = "public.user_status";
    String T_USER_STATUS_C_USER_ID = "user_id";
    String T_USER_STATUS_C_FROZEN = "frozen";
    String T_USER_STATUS_C_BANNED = "banned";

}
