package com.prutech.mailsender.service;

import java.util.List;
import java.util.Optional;

import com.prutech.mailsender.model.MailServerDetails;

public interface MailServerDetailsService {

	public MailServerDetails saveMailServerDetails(MailServerDetails mailServerDetails);

	public MailServerDetails updateMailServerDetails(MailServerDetails mailServerDetails);

	public Optional<MailServerDetails> getMailServerDetailsById(String mailServerDetailsId);

	public MailServerDetails deleteMailServerDetails(MailServerDetails mailServerDetails);
	
	public List<MailServerDetails> getAllActiveMailServerDetails();
	
	public Optional<MailServerDetails> getMailServerDetailsByOrganizationId(String organizationId);

}
