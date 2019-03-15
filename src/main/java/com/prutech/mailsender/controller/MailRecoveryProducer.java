package com.prutech.mailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prutech.mailsender.service.MailRecoveryService;

@RestController
@RequestMapping("/api/v1/mailRecoveryProducer")
public class MailRecoveryProducer {

	@Autowired
	private MailRecoveryService mailRecoveryService;

	@RequestMapping(value = "/loadAllRecoveryMailsIntoActiveMQ", method = RequestMethod.POST)
	public void loadAllRecoveryMailsIntoActiveMQ() {
		mailRecoveryService.saveAllRecoveryMailsIntoActiveMQ();
		System.out.println(
				"MailRecoveryController.loadAllRecoveryMailsIntoActiveMQ()...loaded all recovery mails into Active MQ.");
	}
}
