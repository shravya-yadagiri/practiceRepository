package com.prutech.mailsender.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prutech.mailsender.dto.MailSenderDTO;

public interface MailSenderService {

	public MailSenderDTO sendMail(MailSenderDTO mailSenderDTO) throws JsonProcessingException;

}
