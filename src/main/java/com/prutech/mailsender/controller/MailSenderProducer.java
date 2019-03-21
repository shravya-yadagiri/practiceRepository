package com.prutech.mailsender.controller;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prutech.mailsender.dto.MailSenderDTO;

@RestController
@RequestMapping("/api/v1/mailSenderProducer")
public class MailSenderProducer {

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Queue mailSenderQueue;

	@RequestMapping(value = "/sendSimpleMail", method = RequestMethod.POST)
	public String sendSimpleMail(@RequestBody MailSenderDTO mailSenderDTO) {
		jmsTemplate.convertAndSend(mailSenderQueue, mailSenderDTO);
		System.out.println("MailSenderProducer.sendSimpleMail()...added to queue!");
		return "Added to queue successfully.";
	}
}
