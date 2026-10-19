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
            JobDataMap data = new JobDataMap();
            data.put("param1", "Hello, Rabbit!");
            data.put("param2", 42);
            JobDetail job = newJob(Rabbit.class)
                    .setJobData(data)
                    .build();
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
            String param1 = context.getJobDetail().getJobDataMap().getString("param1");
            int param2 = context.getJobDetail().getJobDataMap().getInt("param2");
            System.out.println("Rabbit runs here with param1: " + param1 + " and param2: " + param2);
        }
    }
}