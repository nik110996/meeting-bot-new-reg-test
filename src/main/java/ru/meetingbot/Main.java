package ru.meetingbot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.meetingbot.admin.AdminConsole;
import ru.meetingbot.deamon.DeamonThread;
import ru.meetingbot.util.PropertiesUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.LogManager;

public class Main {

    /**
     * Настройка логирования
     */
    private static void initLogging() {
        File file = Path.of("./", "logging.properties").toFile();

        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            LogManager.getLogManager().readConfiguration(fileInputStream);

        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }

    /**
     * Настройка локали. Файл string.properties с английским языком по страндарту
     */
    private static void initResourceBundle() {
        String locale = PropertiesUtils.getProperties("bot").getProperty("locale");
        new Locale("xx", "YY");

        /* при добавлани новой локали strings_xx_YY.properties
         * добавляется новый case
         */
        switch (locale) {
            /*case "xx_YY":
                ResBundle.setLocale(new Locale("xx", "YY"));
                break;*/
        }
    }

    /**
     * Включение бота
     */
    private static void initTelegramBotsApi(MeetingBot bot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initLogging();
        initResourceBundle();

        MeetingBot bot = new MeetingBot();

        initTelegramBotsApi(bot);

        // консоль админа, только во время разработки
//         new AdminConsole(bot);

        // недельный цикл
        new DeamonThread();
    }

}