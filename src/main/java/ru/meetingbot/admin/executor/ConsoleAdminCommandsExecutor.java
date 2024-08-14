package ru.meetingbot.admin.executor;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import ru.meetingbot.admin.AdminCommand;
import ru.meetingbot.chat.ChatWork;
import ru.meetingbot.db.dao.ChatDAO;
import ru.meetingbot.db.dao.MeetingDAO;
import ru.meetingbot.db.dao.UserDAO;

import java.util.Scanner;

public final class ConsoleAdminCommandsExecutor extends AdminExecutor implements AdminCommand {

    @Override
    protected void sundayPartOne() {
        System.out.println("Воскресенье. Опрос \"Как прошла встреча?\"");
        super.sundayPartOne();
    }

    @Override
    protected void sundayPartTwo() {
        System.out.println("Воскресенье. Опрос \"Кто хочет встретиться на следующей неделе?\"");
        super.sundayPartTwo();
    }

    @Override
    protected void monday() {
        System.out.println("Понедельник. Поиск партнёров");
        super.monday();
    }

    @Override
    protected void wednesdayPartOne() {
        System.out.println("Среда. Опрос \"Договорились о встрече?\"");
        super.wednesdayPartOne();
    }

    @Override
    protected void wednesdayPartTwo() {
        System.out.println("Среда. Повторный поиск партнёров");
        super.wednesdayPartTwo();
    }

    @Override
    protected void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите сообщение для рассылки пользователям: ");
        String nextLine = scanner.nextLine();

        new ChatDAO().getAll().forEach(chat -> {
            ChatWork.sendMessage(chat.getUserId(), nextLine, ParseMode.MARKDOWNV2);
        });
        System.out.println("Сообщение отправлено.");
    }

    @Override
    protected void getCountOfUsers() {
        int countUser = new UserDAO().getCountUser();
        System.out.println("Всего пользователей: " + countUser);
    }

    @Override
    protected void getCountOfUsersOnWeek() {
        int countUserOnWeek = new MeetingDAO().getCountUserOnWeek();
        System.out.println("Всего пользователей, участвующих во встречах на этой неделе: " + countUserOnWeek);
    }

    @Override
    protected void notCommand() {
        System.out.println("Это не является командой.");
    }

    @Override
    protected void help() {
        System.out.println(getHelp());
    }

    protected void getStatisticAllMeeting() {}
    protected void ban() {}
    protected void freeze() {}
    protected void unfreeze() {}

    @Override
    public boolean execute(String command) {
        return super.execute(command);
    }
}
