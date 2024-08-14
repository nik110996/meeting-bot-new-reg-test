package ru.meetingbot.util.logging;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MeetingMeConsoleFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        int level = record.getLevel().intValue();

        // если содержит Throwable, то считать как другой уровень логирования
        if (record.getThrown() != null) {
            level = 901;
        }

        switch (level) {
            case 800: // INFO
                builder.append("\u001B[92m");
                break;
            case 900: // WARNING
                builder.append("\u001B[93m");
                break;
            case 901: // CUSTOM ERROR
                builder.append("\u001B[91m");
                break;
            default:
                builder.append("\u001B[97m");
        }

        builder.append(record.getInstant().truncatedTo(ChronoUnit.MILLIS).toString()).append(" | ");


        switch (level) {
            case 901: // CUSTOM ERROR
                builder.append("ERROR");
                break;
            default:
                builder.append(record.getLevel());
        }

        builder.append(" ")
            .append(record.getLoggerName()).append(" ")
            .append(record.getSourceMethodName()).append("():\n\t\t")
            .append(record.getMessage())
            .append((level == 901) ? "\n\t\t" + Arrays.toString(record.getThrown().getStackTrace()) : " ")
            .append("\u001B[0m")
            .append("\n");

        return builder.toString();
    }
}
