package ru.meetingbot.deamon.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.meetingbot.deamon.day.SundayPartOne;

public class SundayPartOneJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        new SundayPartOne().execute();
    }
}
