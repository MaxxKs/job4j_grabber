package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import java.io.InputStream;
import java.util.Properties;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Properties properties = init();
            String intervalRaw = properties.getProperty("rabbit.interval");
            if (intervalRaw == null) {
                throw new IllegalStateException("rabbit.interval not found in rabbit.properties");
            }
            int interval = Integer.parseInt(intervalRaw);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            throw new IllegalStateException("Quartz scheduler failed", se);
        }
    }

    private static Properties init() {
        Properties properties = new Properties();
        try (InputStream input = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return properties;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }
    }
}