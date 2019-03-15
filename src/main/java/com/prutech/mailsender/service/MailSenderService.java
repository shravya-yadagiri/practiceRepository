package com.prutech.mailsender.service;

import com.prutech.mailsender.dto.MailSenderDTO;
import com.prutech.mailsender.model.MailRecovery;

public interface MailSenderService {

	public MailSenderDTO sendMail(MailSenderDTO mailSenderDTO);

	/**
	 * @param mailRecovery
	 */
	public void sendMailUsingRecovery(MailRecovery mailRecovery);

}
