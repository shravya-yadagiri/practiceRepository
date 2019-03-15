package com.prutech.mailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.prutech.mailsender.model.MailRecovery;
import com.prutech.mailsender.service.MailSenderService;

@Component
public class MailRecoveryConsumer {

	@Autowired
	MailSenderService mailSenderService;

	@JmsListener(destination = "${activemq.recoverymails-queue}")
	public void listener(MailRecovery mailRecovery) {
		mailSenderService.sendMailUsingRecovery(mailRecovery);
		System.out.println("MailRecoveryConsumer.listener()...called sendMailUsingRecovery.");
	}
}
