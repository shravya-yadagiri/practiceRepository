package com.prutech.mailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prutech.mailsender.dto.MailSenderDTO;
import com.prutech.mailsender.service.MailSenderService;

@Component
public class MailRecoveryConsumer {

	@Autowired
	MailSenderService mailSenderService;

	@JmsListener(destination = "${activemq.recoverymails-queue}")
	public void listener(MailSenderDTO mailSenderDTO) throws JsonProcessingException {
		mailSenderService.sendMail(mailSenderDTO);
		System.out.println("MailRecoveryConsumer.listener()...called sendMailUsingRecovery.");
	}
}
