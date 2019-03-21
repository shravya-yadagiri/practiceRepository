package com.prutech.mailsender.service;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.prutech.mailsender.model.MailRecovery;

public interface MailRecoveryService {

	public MailRecovery saveMailRecovery(MailRecovery mailRecovery);
	
	public void saveAllRecoveryMailsIntoActiveMQ() throws JsonParseException, JsonMappingException, IOException;

}
