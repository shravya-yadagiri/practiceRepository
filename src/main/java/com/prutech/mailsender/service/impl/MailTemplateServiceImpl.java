package com.prutech.mailsender.service.impl;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.prutech.mailsender.dao.MailTemplateRepository;
import com.prutech.mailsender.model.MailTemplate;
import com.prutech.mailsender.model.ResponseData;
import com.prutech.mailsender.model.StatusEnum;
import com.prutech.mailsender.service.MailTemplateService;
import com.prutech.mailsender.util.MailSenderUtil;

@Service
public class MailTemplateServiceImpl implements MailTemplateService {

	@Autowired
	private MailTemplateRepository mailTemplateRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailTemplate saveTemplate(MailTemplate mailTemplate) {
		if (mailTemplate.getMailSubject() != null) {
			mailTemplate.setMailSubject(MailSenderUtil.encodeStringUsingBase64(mailTemplate.getMailSubject()));
		}
		if (mailTemplate.getMailBody() != null) {
			mailTemplate.setMailBody(MailSenderUtil.encodeStringUsingBase64(mailTemplate.getMailBody()));
		}
		mailTemplate.setStatus(StatusEnum.ACTIVE.getStatusCode());
		mailTemplate.setCreatedDate(new Date());
		mailTemplate.setLastModifiedDate(new Date());
		mailTemplateRepository.save(mailTemplate);
		mailTemplate.appendData(new ResponseData("status", "success"));
		return mailTemplate;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailTemplate updateTemplate(MailTemplate mailTemplate) {
		Optional<MailTemplate> templateOptional = mailTemplateRepository.findById(mailTemplate.getMailTemplateId());
		if (templateOptional.isPresent()) {
			mailTemplateRepository.save(mailTemplate);
			mailTemplate.appendData(new ResponseData("status", "success"));
		} else {
			mailTemplate.appendData(new ResponseData("status", "failed"));
		}
		return mailTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.mailsender.service.MailTemplateService#
	 * getTemplateById(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Optional<MailTemplate> getTemplateById(String templateId) {
		return mailTemplateRepository.findById(templateId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailTemplate deleteTemplate(MailTemplate mailTemplate) {
		Optional<MailTemplate> templateOptional = mailTemplateRepository.findById(mailTemplate.getMailTemplateId());
		if (templateOptional.isPresent()) {
			mailTemplate.setStatus(StatusEnum.INACTIVE.getStatusCode());
			mailTemplateRepository.save(mailTemplate);
			mailTemplate.appendData(new ResponseData("status", "success"));
		} else {
			mailTemplate.appendData(new ResponseData("status", "failed"));
		}
		return mailTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.mailsender.service.MailTemplateService#
	 * getTemplateByActionAndOrganizationId(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Optional<MailTemplate> getTemplateByActionAndOrganizationId(String action, String organizationId) {
		return mailTemplateRepository.findByActionAndOrganizationId(action, organizationId);
	}
}
