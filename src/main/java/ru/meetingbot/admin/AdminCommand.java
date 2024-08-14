package ru.meetingbot.admin;

public interface AdminCommand {

    String A_HELP = "/help";

    String A_SUNDAY_PART_ONE = "/s1";
    String A_SUNDAY_PART_TWO = "/s2";
    String A_MONDAY = "/m";
    String A_WEDNESDAY_PART_ONE = "/w1";
    String A_WEDNESDAY_PART_TWO = "/w2";

    String A_SEND_MESSAGE = "/send_message";

    String A_BAN_USER = "/ban_user";
    String A_FREEZE_USER = "/freeze_user";
    String A_UNFREEZE_USER = "/unfreeze_user";

    String A_GET_COUNT_OF_USERS = "/get_count_of_users";
    String A_GET_COUNT_OF_USERS_ON_WEEK = "/get_count_of_users_on_week";

    String A_GET_STATISTIC_ALL_MEETING = "/get_statistic_all_meeting";

    default String getHelp() {
        StringBuilder builder = new StringBuilder();
        builder.append(A_HELP).append(" - вывод всех команд.").append("\n");
        builder.append("\n");
        builder.append(A_BAN_USER).append(" - забанить пользователя по username").append("\n");
        builder.append(A_FREEZE_USER).append(" - отключить участие пользователя во встречах").append("\n");
        builder.append(A_UNFREEZE_USER).append(" - включить участие пользователя во встречах").append("\n");
        builder.append(A_SEND_MESSAGE).append(" - рассылка сообщения пользователям").append("\n");
        builder.append(A_GET_COUNT_OF_USERS).append(" - показать количество всех пользователей").append("\n");
        builder.append(A_GET_COUNT_OF_USERS_ON_WEEK).append(" - показать количество всех пользователей, участвующих во встречах на этой неделе.").append("\n");
        builder.append(A_GET_STATISTIC_ALL_MEETING).append(" - показать статистику во всем встречам.");

        return builder.toString();
    }
}
