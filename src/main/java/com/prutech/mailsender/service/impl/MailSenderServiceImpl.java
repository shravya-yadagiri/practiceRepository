package com.prutech.mailsender.service.impl;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prutech.mailsender.dto.MailSenderDTO;
import com.prutech.mailsender.model.MailAuditLog;
import com.prutech.mailsender.model.MailRecovery;
import com.prutech.mailsender.model.MailServerDetails;
import com.prutech.mailsender.model.MailTemplate;
import com.prutech.mailsender.model.ResponseData;
import com.prutech.mailsender.model.StatusEnum;
import com.prutech.mailsender.service.MailAuditLogService;
import com.prutech.mailsender.service.MailRecoveryService;
import com.prutech.mailsender.service.MailSenderService;
import com.prutech.mailsender.service.MailServerDetailsService;
import com.prutech.mailsender.service.MailTemplateService;
import com.prutech.mailsender.util.MailSenderUtil;

@Service
public class MailSenderServiceImpl implements MailSenderService {

	@Autowired
	private MailServerDetailsService mailServerDetailsService;

	@Autowired
	private MailTemplateService mailTemplateService;

	@Autowired
	private MailAuditLogService mailAuditLogService;

	@Autowired
	private MailRecoveryService mailRecoveryService;

	@Async
	public MailSenderDTO sendMail(MailSenderDTO mailSenderDTO) throws JsonProcessingException {

		MailServerDetails mailServerDetails = null;
		MailTemplate mailTemplate = null;
		JavaMailSenderImpl mailSender = null;

		String organizationId = null;
		String action = null;
		Map<String, Object> model = null;

		try {

			organizationId = mailSenderDTO.getOrganizationId();
			System.out.println("MailSenderServiceImpl.sendMail()...organizationId:" + organizationId);

			action = mailSenderDTO.getAction();
			System.out.println("MailSenderServiceImpl.sendMail()...action:" + action);

			model = mailSenderDTO.getModel();
			System.out.println("MailSenderServiceImpl.sendMail()...model:" + model);

			/**
			 * Need to check the last modified date and store fresh one if new exists
			 */

			mailServerDetails = getMailServerDetails(organizationId);
			System.out.println("MailSenderServiceImpl.sendMail()...mailServerDetails:" + mailServerDetails);

			mailTemplate = getTemplateDetails(action, organizationId);
			System.out.println("MailSenderServiceImpl.sendMail()...mailTemplate:" + mailTemplate);

			if (mailServerDetails != null && mailTemplate != null) {

				if (MailSenderUtil.mailSenders.containsKey(organizationId)) {
					mailSender = MailSenderUtil.mailSenders.get(organizationId);
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailSender:" + mailSender);

				if (model.get("toAddress") != null) {
					List<String> mailToAddress = (List<String>) model.get("toAddress");
					if (mailToAddress != null && mailToAddress.size() > 0) {
						setAllPropertiesAndSendMail(organizationId, action, model, mailTemplate, mailServerDetails,
								mailSender);
						mailSenderDTO.appendData(new ResponseData("status", "success"));
					} else {
						insertMailAuditLog(organizationId, action, model, null, null,
								MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailSubject()),
								MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailBody()),
								"To field is not specified");
						insertIntoMailRecovery(organizationId, action, model, "To field is not specified");
						mailSenderDTO.appendData(new ResponseData("status", "failed"));
					}
				} else {
					insertMailAuditLog(organizationId, action, model, null, null,
							MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailSubject()),
							MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailBody()),
							"To field is not specified");
					insertIntoMailRecovery(organizationId, action, model, "To field is not specified");
					mailSenderDTO.appendData(new ResponseData("status", "failed"));
				}
			} else {
				insertMailAuditLog(organizationId, action, model, null, null, null, null,
						"Mail Server Details/Template details are not available");
				insertIntoMailRecovery(organizationId, action, model,
						"Mail Server Details/Template details are not available");
				mailSenderDTO.appendData(new ResponseData("status", "failed"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			insertIntoMailRecovery(organizationId, action, model, e.getMessage());
			mailSenderDTO.appendData(new ResponseData("status", "failed"));
		}
		return mailSenderDTO;
	}

	public MailServerDetails getMailServerDetails(String organizationId) {
		checkAndStoreFreshMailServerDetails(organizationId);
		return MailSenderUtil.mailServerDetails.get(organizationId);
	}

	public MailTemplate getTemplateDetails(String action, String organizationId) {
		checkAndStoreFreshMailTemplate(organizationId, action);
		return MailSenderUtil.templatesMap.get(organizationId + "|" + action);
	}

	public void setAllPropertiesAndSendMail(String organizationId, String action, Map<String, Object> model,
			MailTemplate mailTemplate, MailServerDetails mailServerDetails, JavaMailSenderImpl mailSender)
			throws JsonProcessingException {

		MimeMessage message = null;
		MimeMessageHelper helper = null;

		String mailFrom = null;
		List<String> mailToAddress = null;
		String mailCc = null;
		String mailBcc = null;
		String mailBody = null;
		String mailSubject = null;
		String mailSentHeader = null;
		String mailToAddressString = null;

		try {
			message = mailSender.createMimeMessage();
			helper = new MimeMessageHelper(message, true);

			if (model != null && mailTemplate != null && mailServerDetails != null) {

				// Multiple ToAddress
				if (model.get("toAddress") != null) {
					mailToAddress = (List<String>) model.get("toAddress");
				}
				System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailToAddress:" + mailToAddress);

				// Cc
				if (mailTemplate.getMailCc() != null && mailTemplate.getMailCc().trim().length() > 0) {
					mailCc = mailTemplate.getMailCc();
				}
				System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailCc:" + mailCc);

				// Bcc
				if (mailTemplate.getMailBcc() != null && mailTemplate.getMailBcc().trim().length() > 0) {
					mailBcc = mailTemplate.getMailBcc();
				}
				System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailBcc:" + mailBcc);

				// From
				if (mailServerDetails.getMailFromMailId() != null
						&& mailServerDetails.getMailFromMailId().trim().length() > 0) {
					mailFrom = mailServerDetails.getMailFromMailId();
				}
				System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailFrom:" + mailFrom);

				// Subject
				if (mailTemplate.getMailSubject() != null && mailTemplate.getMailSubject().trim().length() > 0) {
					mailSubject = MailSenderUtil.getReplacedHtmlContent(mailTemplate.getMailSubject(), model);
				}
				System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailSubject:" + mailSubject);

				// Body
				if (mailTemplate.getMailBody() != null && mailTemplate.getMailBody().trim().length() > 0) {
					mailBody = MailSenderUtil.getReplacedHtmlContent(mailTemplate.getMailBody(), model);
				}
				System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailBody:" + mailBody);
			}

			setPropertiesForHelper(helper, mailToAddress.toArray(new String[0]), mailCc, mailBcc, mailFrom, mailSubject,
					mailBody);

			mailSender.send(message);

			mailSentHeader = prepareMailSentHeader(message);
			System.out.println("MailSenderServiceImpl.setAllPropertiesAndSendMail()...mailSentHeader:" + mailSentHeader);

			if (mailToAddress != null && mailToAddress.size() > 0) {
				mailToAddressString = "";
				for (String eachMailId : mailToAddress) {
					mailToAddressString = mailToAddressString + eachMailId;
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			insertMailAuditLog(organizationId, action, model, mailToAddressString, mailSentHeader, mailSubject,
					mailBody, null);
		}
	}

	public void setPropertiesForHelper(MimeMessageHelper helper, String[] mailToAddress, String mailCc, String mailBcc,
			String mailFrom, String mailSubject, String mailBody) {

		try {
			helper.setTo(mailToAddress);
			helper.setCc(mailCc);
			helper.setBcc(mailBcc);
			helper.setFrom(mailFrom);
			helper.setSubject(mailSubject);
			helper.setText(mailBody, true);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void insertMailAuditLog(String organizationId, String action, Map<String, Object> model,
			String mailToAddress, String mailSentHeader, String mailSubject, String mailBody, String comments)
			throws JsonProcessingException {
		MailAuditLog mailAuditLog = new MailAuditLog();

		mailAuditLog.setOrganizationId(organizationId);
		mailAuditLog.setAction(action);
		mailAuditLog.setModel(MailSenderUtil.convertObjectToJson(model));
		mailAuditLog.setMailToAddress(mailToAddress);
		mailAuditLog.setMailSentHeader(mailSentHeader);
		mailAuditLog.setMailSubject(mailSubject);
		mailAuditLog.setMailBody(mailBody);
		mailAuditLog.setComments(comments);

		mailAuditLog.setStatus(StatusEnum.ACTIVE.getStatusCode());

		mailAuditLogService.saveMailAuditLog(mailAuditLog);
	}

	public void insertIntoMailRecovery(String organizationId, String action, Map<String, Object> model, String comments)
			throws JsonProcessingException {

		MailRecovery mailRecovery = new MailRecovery();
		mailRecovery.setOrganizationId(organizationId);
		mailRecovery.setAction(action);
		mailRecovery.setModel(MailSenderUtil.convertObjectToJson(model));
		mailRecovery.setComments(comments);
		mailRecovery.setStatus(StatusEnum.ACTIVE.getStatusCode());

		mailRecoveryService.saveMailRecovery(mailRecovery);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.prutech.mailsender.service.MailSenderService#sendMailUsingRecovery(com.
	 * prutech.mailsender.model.MailRecovery)
	 */
	public String prepareMailSentHeader(MimeMessage message) {
		String mailSentHeader = null;
		try {
			Enumeration<String> msgHeaderEnumeration = message.getAllHeaderLines();
			StringBuilder messageHeaderFullMessage = new StringBuilder();
			while (msgHeaderEnumeration.hasMoreElements()) {
				messageHeaderFullMessage.append(msgHeaderEnumeration.nextElement());
				messageHeaderFullMessage.append(" ");
			}
			if (messageHeaderFullMessage != null) {
				mailSentHeader = messageHeaderFullMessage.toString();
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return mailSentHeader;

	}

	public void checkAndStoreFreshMailServerDetails(String organizationId) {
		Optional<MailServerDetails> mailServerDetailsOptional = mailServerDetailsService
				.getMailServerDetailsByOrganizationId(organizationId);
		if (mailServerDetailsOptional.isPresent()) {
			MailServerDetails mailServerDetails = mailServerDetailsOptional.get();

			if (MailSenderUtil.mailServerDetailsLastModifiedDates.containsKey(organizationId)
					&& !(MailSenderUtil.mailServerDetailsLastModifiedDates.get(organizationId)
							.equals(mailServerDetails.getLastModifiedDate()))
					|| !MailSenderUtil.mailServerDetailsLastModifiedDates.containsKey(organizationId)) {

				// Storing fresh MailServerDetails
				MailSenderUtil.mailServerDetails.put(organizationId, mailServerDetails);

				// Storing fresh JavaMailSenderImpl
				MailSenderUtil.mailSenders.put(mailServerDetails.getOrganizationId(),
						MailSenderUtil.createMailSender(mailServerDetails));

				// Storing fresh last modified date
				MailSenderUtil.mailServerDetailsLastModifiedDates.put(mailServerDetails.getOrganizationId(),
						mailServerDetails.getLastModifiedDate());
			}
		}
	}

	public void checkAndStoreFreshMailTemplate(String organizationId, String action) {
		Optional<MailTemplate> mailTemplateOptional = mailTemplateService.getTemplateByActionAndOrganizationId(action,
				organizationId);
		if (mailTemplateOptional.isPresent()) {
			MailTemplate mailTempalte = mailTemplateOptional.get();

			if (MailSenderUtil.templatesLastModifiedDates.containsKey(organizationId + "|" + action)
					&& !(MailSenderUtil.templatesLastModifiedDates.get(organizationId + "|" + action)
							.equals(mailTempalte.getLastModifiedDate()))
					|| !MailSenderUtil.templatesLastModifiedDates.containsKey(organizationId + "|" + action)) {

				// Storing fresh MailTemplate
				MailSenderUtil.templatesMap.put(organizationId + "|" + action, mailTempalte);

				// Storing fresh last modified date
				MailSenderUtil.templatesLastModifiedDates.put(organizationId + "|" + action,
						mailTempalte.getLastModifiedDate());
			}
		}
	}
}