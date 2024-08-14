package ru.meetingbot.deamon.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.meetingbot.deamon.day.WednesdayPartOne;

public class WednesdayPartOneJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        new WednesdayPartOne().execute();
    }
}
