package ru.meetingbot.admin.executor;

import ru.meetingbot.admin.AdminCommand;
import ru.meetingbot.deamon.day.*;

abstract class AdminExecutor implements AdminCommand {

    protected void sundayPartOne() {
        new SundayPartOne().execute();
    }
    protected void sundayPartTwo() {
        new SundayPartTwo().execute();
    }
    protected void monday() {
        new Monday().execute();
    }
    protected void wednesdayPartOne() {
        new WednesdayPartOne().execute();
    }
    protected void wednesdayPartTwo() {
        new WednesdayPartTwo().execute();
    }

    protected abstract void getStatisticAllMeeting();
    protected abstract void getCountOfUsers();
    protected abstract void getCountOfUsersOnWeek();
    protected abstract void ban();
    protected abstract void freeze();
    protected abstract void unfreeze();
    protected abstract void sendMessage();
    protected abstract void notCommand();
    protected abstract void help();

    public boolean execute(String command) {
        boolean isAdminCommand = true;

        switch (command) {
            /* управление днями */
            case A_SUNDAY_PART_ONE -> sundayPartOne();
            case A_SUNDAY_PART_TWO -> sundayPartTwo();
            case A_MONDAY -> monday();
            case A_WEDNESDAY_PART_ONE -> wednesdayPartOne();
            case A_WEDNESDAY_PART_TWO -> wednesdayPartTwo();

            /* рассылка сообщений */
            case A_SEND_MESSAGE -> sendMessage();

            case A_BAN_USER -> ban();
            case A_FREEZE_USER -> freeze();
            case A_UNFREEZE_USER -> unfreeze();
            case A_GET_COUNT_OF_USERS -> getCountOfUsers();
            case A_GET_COUNT_OF_USERS_ON_WEEK -> getCountOfUsersOnWeek();
            case A_GET_STATISTIC_ALL_MEETING -> getStatisticAllMeeting();

            case A_HELP -> help();
            default -> {
                notCommand();
                isAdminCommand = false;
            }
        }

        return isAdminCommand;
    }
}
