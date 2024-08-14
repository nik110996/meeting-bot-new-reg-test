package ru.meetingbot.admin;


import org.telegram.telegrambots.meta.api.objects.*;
import ru.meetingbot.MeetingBot;
import ru.meetingbot.util.PropertiesUtils;

import java.util.Properties;

public class AdminTestBot extends MeetingBot {

    private final String BOT_NAME;
    private final String BOT_TOKEN;

    public AdminTestBot() {
        Properties properties = PropertiesUtils.getProperties("bot");
        BOT_NAME = properties.getProperty("name");
        BOT_TOKEN = properties.getProperty("token");
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

    }
}