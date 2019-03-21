package com.prutech.mailsender.job;

import java.io.IOException;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.prutech.mailsender.service.MailRecoveryService;

@Component
public class StoreRecoveryMailsIntoActiveMQ extends QuartzJobBean {

	@Autowired
	private MailRecoveryService mailRecoveryService;

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		System.out.println("StoreRecoveryMailsIntoActiveMQ.executeInternal()...is in execution...");
		try {
			mailRecoveryService.saveAllRecoveryMailsIntoActiveMQ();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
