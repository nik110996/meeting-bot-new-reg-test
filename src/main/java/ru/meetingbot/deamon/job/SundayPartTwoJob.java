package ru.meetingbot.deamon.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.meetingbot.deamon.day.SundayPartTwo;

public class SundayPartTwoJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        new SundayPartTwo().execute();
    }
}
