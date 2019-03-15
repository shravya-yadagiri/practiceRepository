package com.prutech.mailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.prutech.mailsender.dto.MailSenderDTO;
import com.prutech.mailsender.service.MailSenderService;

@Component
public class MailSenderConsumer {

	@Autowired
	MailSenderService mailSenderService;

	@JmsListener(destination = "${activemq.mailsender-queue}")
	public void listener(MailSenderDTO mailsenderDTO) {
		mailSenderService.sendMail(mailsenderDTO);
		System.out.println("MailSenderProducer.sendSimpleMail()...called mail send logic.");
	}
}
