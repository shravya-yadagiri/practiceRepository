package com.prutech.mailsender.controller;

import java.util.UUID;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prutech.mailsender.job.StoreRecoveryMailsIntoActiveMQ;

@RestController
@RequestMapping("/api/v1/mailRecoveryJobSchedule")
public class MailRecoveryJobScheduleController {

	@Autowired
	private Scheduler scheduler;

	@PostMapping("/scheduleRecoveryJob")
	public void scheduleRecoveryJob(@RequestParam("jobCronParameters") String jobCronParameters) {
		System.out.println("MailRecoveryJobScheduleController.scheduleRecoveryJob()...is in execution...");
		try {
			JobDetail jobDetail = buildJobDetail();
			System.out.println("JobDetais values are set to pass values to Scheduler");
			Trigger trigger = buildJobTrigger(jobDetail, jobCronParameters);
			System.out.println("Trigger values are set to pass values to Scheduler");
			scheduler.scheduleJob(jobDetail, trigger);
			System.out.println("JobDetails,Trigger values are passed to Scheduler");

		} catch (SchedulerException ex) {
			ex.printStackTrace();
		}
	}

	private JobDetail buildJobDetail() {
		return JobBuilder.newJob(StoreRecoveryMailsIntoActiveMQ.class)
				.withIdentity(UUID.randomUUID().toString(), "recovery-jobs").withDescription("Schedule Recovery Job")
				/* .usingJobData(jobDataMap) */.storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, String jobCronParameters) {
		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "recovery-jobs-triggers")
				.withDescription("Schedule Recovery Job Trigger")
				/* .startAt(Date.from(startAt.toInstant())) */
				/*
				 * .withSchedule(SimpleScheduleBuilder.simpleSchedule().
				 * withMisfireHandlingInstructionFireNow())
				 */

				.withSchedule(CronScheduleBuilder.cronSchedule(jobCronParameters)).build();
	}
}
