package com.prutech.mailsender.service;

import com.prutech.mailsender.model.MailRecovery;

public interface MailRecoveryService {

	public MailRecovery saveMailRecovery(MailRecovery mailRecovery);
	
	public void saveAllRecoveryMailsIntoActiveMQ();

}
