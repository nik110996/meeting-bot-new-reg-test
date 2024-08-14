package ru.meetingbot.deamon;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.meetingbot.MeetingBot;
import ru.meetingbot.deamon.job.*;

import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class DeamonThread {

    private static final Logger logger = Logger.getLogger(DeamonThread.class.getName());
    private static final String GROUP = "week";
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Singapore");

    private Scheduler sched;

    private void initScheduler() {
        SchedulerFactory schedFact = new StdSchedulerFactory();

        try {
            sched = schedFact.getScheduler();
            sched.start();
        } catch (SchedulerException e) {
            logger.log(Level.WARNING, "Ошибка при запуске Scheduler", e);
        }
    }

    public DeamonThread() {
        initScheduler();

        JobDetail sundayOneJob = createJobDetail(SundayPartOneJob.class, "sundayOneJob");
        JobDetail sundayTwoJob = createJobDetail(SundayPartTwoJob.class, "sundayTwoJob");
        JobDetail mondayJob = createJobDetail(MondayJob.class, "mondayJob");
        JobDetail wednesdayOneJob = createJobDetail(WednesdayPartOneJob.class, "wednesdayOneJob");
        JobDetail wednesdayTwoJob = createJobDetail(WednesdayPartTwoJob.class, "wednesdayTwoJob");

        CronTrigger sundayOneTrigger = createTrigger("sundayOneTrigger", DateBuilder.SUNDAY, 12, 0);
        CronTrigger sundayTwoTrigger = createTrigger("sundayTwoTrigger", DateBuilder.SUNDAY, 17, 0);
        CronTrigger mondayTrigger = createTrigger("mondayTrigger", DateBuilder.MONDAY, 12, 0);
        CronTrigger wednesdayOneTrigger = createTrigger("wednesdayOneTrigger", DateBuilder.WEDNESDAY, 12, 0);
        CronTrigger wednesdayTwoTrigger = createTrigger("wednesdayTwoTrigger", DateBuilder.WEDNESDAY, 17, 0);

        try {
            sched.scheduleJob(sundayOneJob, sundayOneTrigger);
            sched.scheduleJob(sundayTwoJob, sundayTwoTrigger);
            sched.scheduleJob(mondayJob, mondayTrigger);
            sched.scheduleJob(wednesdayOneJob, wednesdayOneTrigger);
            sched.scheduleJob(wednesdayTwoJob, wednesdayTwoTrigger);
        } catch (SchedulerException e) {
            logger.log(Level.WARNING, "Ошибка при добавлении работы в Scheduler", e);
        }
    }

    private CronTrigger createTrigger(String identity, int day, int hour, int minute) {
        return newTrigger().withIdentity(identity, GROUP)
                .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(day, hour, minute).inTimeZone(TIME_ZONE)).startNow().build();
    }

    private JobDetail createJobDetail(Class<? extends Job> classObject, String identity) {
        return newJob(classObject).withIdentity(identity, GROUP).build();
    }
}


