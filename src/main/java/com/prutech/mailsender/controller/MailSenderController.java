package com.prutech.mailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prutech.mailsender.dto.MailSenderDTO;
import com.prutech.mailsender.service.MailSenderService;

@RestController
@RequestMapping("/api/v1/mailSender")
public class MailSenderController {

	@Autowired
	MailSenderService mailSenderService;

	@RequestMapping(value = "/sendMail", method = RequestMethod.POST)
	public MailSenderDTO sendSimpleMail(@RequestBody MailSenderDTO mailSenderDTO) {
		return mailSenderService.sendMail(mailSenderDTO);
	}
}
