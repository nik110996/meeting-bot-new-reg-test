package ru.meetingbot.util.logging;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MeetingMeFileFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        int level = record.getLevel().intValue();

        builder.append(record.getInstant().truncatedTo(ChronoUnit.MILLIS).toString()).append(" | ");

        // если содержит Throwable, то считать как другой уровень логирования
        if (record.getThrown() != null) {
            level = 901;
        }

        switch (level) {
            case 901: // CUSTOM ERROR
                builder.append("ERROR");
                break;
            default:
                builder.append(record.getLevel());
        }

        builder.append(" ")
                .append(record.getLoggerName()).append(" ")
                .append(record.getSourceMethodName()).append("(): ")
                .append(record.getMessage())
                .append((level == 901) ? "\n\t\t" + Arrays.toString(record.getThrown().getStackTrace()) : " ")
                .append("\n");

        return builder.toString();
    }
}
