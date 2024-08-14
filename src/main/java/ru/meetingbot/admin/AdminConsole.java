package ru.meetingbot.admin;

import ru.meetingbot.MeetingBot;
import ru.meetingbot.admin.executor.ConsoleAdminCommandsExecutor;
import ru.meetingbot.db.DBConst;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Для тестирования команд через консоль
 */
public class AdminConsole implements Runnable, AdminCommand, DBConst {

    private final MeetingBot bot;
    private final ConsoleAdminCommandsExecutor commandExecutor;

    public AdminConsole(MeetingBot bot) {
        this.bot = bot;
        commandExecutor = new ConsoleAdminCommandsExecutor();

        this.start();
    }

    /* запуск */
    private void start() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(this);
        executor.shutdown();
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String next = scanner.nextLine();
            commandExecutor.execute(next);
        }
    }
}


