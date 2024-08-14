package ru.meetingbot.deamon.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.meetingbot.deamon.day.WednesdayPartTwo;

public class WednesdayPartTwoJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        new WednesdayPartTwo().execute();
    }
}
