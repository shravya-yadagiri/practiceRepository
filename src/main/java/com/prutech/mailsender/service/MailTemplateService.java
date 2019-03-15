package com.prutech.mailsender.service;

import java.util.Optional;

import com.prutech.mailsender.model.MailTemplate;

public interface MailTemplateService {

	public MailTemplate saveTemplate(MailTemplate mailTemplate);

	public MailTemplate updateTemplate(MailTemplate mailTemplate);

	public Optional<MailTemplate> getTemplateById(String templateId);
	
	public Optional<MailTemplate> getTemplateByActionAndOrganizationId(String action, String organizationId);

	public MailTemplate deleteTemplate(MailTemplate mailTemplate);
	
}
