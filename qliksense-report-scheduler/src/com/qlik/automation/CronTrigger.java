
package com.qlik.automation;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.qlik.automation.CronTrigger;


public class CronTrigger {
	final static Logger logger = Logger.getLogger(CronTrigger.class);
	public static void main( String[] args ) throws Exception {
		try{
			PropertyConfigurator.configure("resources/log4j.properties");
			logger.debug("Automation report initiated.");
			JobKey DailyReportJobKey = new JobKey("DailyReportJob", "group1");
			JobDetail DailyReportJob = JobBuilder.newJob(DailyReportJob.class)
					.withIdentity(DailyReportJobKey).build();

			Trigger trigger1 = TriggerBuilder.newTrigger().withIdentity("dummyTriggerName1", "group1")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 15 02 1/1 * ? *")).build();
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(DailyReportJob, trigger1);
		} catch (Exception e) {
			logger.debug(e,e);
			throw e;
		}
		    
    }
}