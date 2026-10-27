package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.job4j.grabber.stores.Store;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

@DisallowConcurrentExecution
public class SuperJobGrab implements Job {
    private final Parse parse = new HabrCareerParse(new HabrCareerDateTimeParser());
    private static final Logger LOG = Logger.getLogger(SuperJobGrab.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("Job started");
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        parse.fetch().forEach(store::save);
        LOG.info("Job finished");
    }
}