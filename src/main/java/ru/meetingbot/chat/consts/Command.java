package ru.meetingbot.chat.consts;

/**
 * Команды, которые может вызвать пользователь.
 */
public interface Command {

    String C_HELP = "/help";
    String C_CANCEL = "/cancel";
    String C_ACCEPT = "/accept";
    String C_DECLINE = "/decline";
    String C_WILL_TRY_AGAIN = "/will_try_again";

    // в понедельник при нажатии на кнопку
    String C_WANT_ON_THIS_WEEK = "/want_on_this_week";

    String C_ONE ="/1";
    String C_TWO ="/2";
    String C_THREE ="/3";
    String C_FOUR ="/4";
    String C_FIVE ="/5";

    String C_START = "/start";
        String C_WRITE_FULL_NAME = "/write_full_name";

    String C_UPDATE_PROFILE = "/update_profile";
        String C_UPDATE_FULL_NAME = "/update_full_name";
        String C_UPDATE_PROFILE_LINK = "/update_profile_link";
        String C_UPDATE_JOB = "/update_job";
        String C_UPDATE_HOBBIE = "/update_hobbie";
        String C_UPDATE_AGE = "/update_age";

    String C_WHO_IS_MY_MEETING_WITH = "/who_is_my_match";
}
