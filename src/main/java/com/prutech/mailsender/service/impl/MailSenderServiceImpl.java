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
	public MailSenderDTO sendMail(MailSenderDTO mailSenderDTO) {

		MailServerDetails mailServerDetails = null;
		MailTemplate mailTemplate = null;
		JavaMailSenderImpl mailSender = null;

		try {

			String organizationId = mailSenderDTO.getOrganizationId();
			System.out.println("MailSenderServiceImpl.sendMail()...organizationId:" + organizationId);

			String action = mailSenderDTO.getAction();
			System.out.println("MailSenderServiceImpl.sendMail()...action:" + action);

			Map<String, Object> model = mailSenderDTO.getModel();
			System.out.println("MailSenderServiceImpl.sendMail()...model:" + model);

			mailServerDetails = getMailServerDetailsOfOrganization(organizationId);
			System.out.println("MailSenderServiceImpl.sendMail()...mailServerDetails:" + mailServerDetails);

			mailTemplate = getTemplateDetails(action, organizationId);
			System.out.println("MailSenderServiceImpl.sendMail()...mailTemplate:" + mailTemplate);

			if (mailServerDetails != null && mailTemplate != null) {

				if (!MailSenderUtil.mailSenderMaps.containsKey(organizationId)) {
					MailSenderUtil.mailSenderMaps.put(organizationId,
							MailSenderUtil.createMailSender(mailServerDetails));
				}

				if (MailSenderUtil.mailSenderMaps.containsKey(organizationId)) {
					mailSender = MailSenderUtil.mailSenderMaps.get(organizationId);
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailSender:" + mailSender);

				if (model.get("toAddress") != null) {
					List<String> mailToAddress = (List<String>) model.get("toAddress");
					if (mailToAddress != null && mailToAddress.size() > 0) {
						setAllPropertiesAndSendMail(organizationId, action, model, mailTemplate, mailServerDetails,
								mailSender);
					} else {
						insertMailAuditLog(organizationId, action, null, null,
								MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailSubject()),
								MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailBody()),
								"To field is not specified");
					}
				} else {
					insertMailAuditLog(organizationId, action, null, null,
							MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailSubject()),
							MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailBody()),
							"To field is not specified");
				}
				mailSenderDTO.appendData(new ResponseData("status", "success"));
			} else {
				insertMailAuditLog(organizationId, action, null, null,
						MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailSubject()),
						MailSenderUtil.decodeStringUsingBase64(mailTemplate.getMailBody()),
						"Mail Server Details/Template details are not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
			mailSenderDTO.appendData(new ResponseData("status", "failed"));
		}
		return mailSenderDTO;
	}

	public MailServerDetails getMailServerDetailsOfOrganization(String organizationId) {
		MailServerDetails mailServerDetails = null;
		Optional<MailServerDetails> mailServerDetailsOptional = mailServerDetailsService
				.getMailServerDetailsByOrganizationId(organizationId);
		if (mailServerDetailsOptional.isPresent()) {
			mailServerDetails = mailServerDetailsOptional.get();
		}
		return mailServerDetails;
	}

	public MailTemplate getTemplateDetails(String action, String organizationId) {
		MailTemplate mailTemplate = null;
		Optional<MailTemplate> templateOptional = mailTemplateService.getTemplateByActionAndOrganizationId(action,
				organizationId);
		if (templateOptional.isPresent()) {
			mailTemplate = templateOptional.get();
		}
		return mailTemplate;
	}

	public void setAllPropertiesAndSendMail(String organizationId, String action, Map<String, Object> model,
			MailTemplate mailTemplate, MailServerDetails mailServerDetails, JavaMailSenderImpl mailSender) {

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
				System.out.println("MailSenderServiceImpl.sendMail()...mailToAddress:" + mailToAddress);

				// Cc
				if (mailTemplate.getMailCc() != null && mailTemplate.getMailCc().trim().length() > 0) {
					mailCc = mailTemplate.getMailCc();
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailCc:" + mailCc);

				// Bcc
				if (mailTemplate.getMailBcc() != null && mailTemplate.getMailBcc().trim().length() > 0) {
					mailBcc = mailTemplate.getMailBcc();
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailBcc:" + mailBcc);

				// From
				if (mailServerDetails.getMailFromMailId() != null
						&& mailServerDetails.getMailFromMailId().trim().length() > 0) {
					mailFrom = mailServerDetails.getMailFromMailId();
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailFrom:" + mailFrom);

				// Subject
				if (mailTemplate.getMailSubject() != null && mailTemplate.getMailSubject().trim().length() > 0) {
					mailSubject = MailSenderUtil.getReplacedHtmlContent(mailTemplate.getMailSubject(), model);
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailSubject:" + mailSubject);

				// Body
				if (mailTemplate.getMailBody() != null && mailTemplate.getMailBody().trim().length() > 0) {
					mailBody = MailSenderUtil.getReplacedHtmlContent(mailTemplate.getMailBody(), model);
				}
				System.out.println("MailSenderServiceImpl.sendMail()...mailBody:" + mailBody);
			}

			setPropertiesForHelper(helper, mailToAddress.toArray(new String[0]), mailCc, mailBcc, mailFrom, mailSubject,
					mailBody);

			mailSender.send(message);

			mailSentHeader = prepareMailSentHeader(message);
			System.out.println("MailSenderServiceImpl.sendMail()...mailSentHeader:" + mailSentHeader);

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
			insertMailAuditLog(organizationId, action, mailToAddressString, mailSentHeader, mailSubject, mailBody,
					null);

			insertMailRecovery(organizationId, action, mailSubject, mailBody, mailFrom, mailToAddressString, mailCc,
					mailBcc, null);
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

	public void insertMailAuditLog(String organizationId, String action, String mailToAddress, String mailSentHeader,
			String mailSubject, String mailBody, String comments) {
		MailAuditLog mailAuditLog = new MailAuditLog();

		mailAuditLog.setOrganizationId(organizationId);
		mailAuditLog.setAction(action);
		/* mailAuditLog.setMailFrom(mailFrom); */
		mailAuditLog.setMailToAddress(mailToAddress);

		/*
		 * mailAuditLog.setMailCc(mailCc); mailAuditLog.setMailBcc(mailBcc);
		 */
		mailAuditLog.setMailSentHeader(mailSentHeader);
		mailAuditLog.setMailSubject(mailSubject);
		mailAuditLog.setMailBody(mailBody);
		mailAuditLog.setComments(comments);

		mailAuditLog.setStatus(StatusEnum.ACTIVE.getStatusCode());

		mailAuditLogService.saveMailAuditLog(mailAuditLog);
	}

	public void insertMailRecovery(String organizationId, String action, String mailSubject, String mailBody,
			String mailFrom, String mailToAddress, String mailCc, String mailBcc, String comments) {

		MailRecovery mailRecovery = new MailRecovery();
		mailRecovery.setOrganizationId(organizationId);
		mailRecovery.setAction(action);
		mailRecovery.setMailSubject(mailSubject);
		mailRecovery.setMailBody(mailBody);
		mailRecovery.setMailFrom(mailFrom);
		mailRecovery.setMailToAddress(mailToAddress);
		mailRecovery.setMailCc(mailCc);
		mailRecovery.setMailBcc(mailBcc);
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
	@Override
	public void sendMailUsingRecovery(MailRecovery mailRecovery) {

		setAllPropertiesAndSendRecoveryMail(mailRecovery.getOrganizationId(), mailRecovery.getAction(), mailRecovery);

	}

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

			System.out.println("MailSenderServiceImpl.sendMail()...mailSentHeader:" + mailSentHeader);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return mailSentHeader;

	}

	public void setAllPropertiesAndSendRecoveryMail(String organizationId, String action, MailRecovery mailRecovery) {

		MimeMessage message = null;
		MimeMessageHelper helper = null;
		JavaMailSenderImpl mailSender = null;

		String mailToAddressString = null;
		String mailSentHeader = null;
		String mailSubject = null;
		String mailBody = null;

		try {
			MailServerDetails mailServerDetails = getMailServerDetailsOfOrganization(organizationId);
			System.out.println("MailSenderServiceImpl.sendMail()...mailServerDetails:" + mailServerDetails);

			MailTemplate mailTemplate = getTemplateDetails(action, organizationId);
			System.out.println("MailSenderServiceImpl.sendMail()...mailTemplate:" + mailTemplate);

			if (!MailSenderUtil.mailSenderMaps.containsKey(organizationId)) {
				MailSenderUtil.mailSenderMaps.put(organizationId, MailSenderUtil.createMailSender(mailServerDetails));
			}

			if (MailSenderUtil.mailSenderMaps.containsKey(organizationId)) {
				mailSender = MailSenderUtil.mailSenderMaps.get(organizationId);
			}

			message = mailSender.createMimeMessage();
			helper = new MimeMessageHelper(message, true);

			String mailToAddress = mailRecovery.getMailToAddress();
			String[] allMailAddresses = mailToAddress.split(",");

			mailSubject = mailRecovery.getMailSubject();
			mailBody = mailRecovery.getMailBody();

			setPropertiesForHelper(helper, allMailAddresses, mailRecovery.getMailCc(), mailRecovery.getMailBcc(),
					mailRecovery.getMailFrom(), mailRecovery.getMailSubject(), mailRecovery.getMailBody());

			mailSender.send(message);

			mailSentHeader = prepareMailSentHeader(message);
			System.out.println("MailSenderServiceImpl.sendMail()...mailSentHeader:" + mailSentHeader);

			if (allMailAddresses != null && allMailAddresses.length > 0) {
				mailToAddressString = "";
				for (String eachMailId : allMailAddresses) {
					mailToAddressString = mailToAddressString + eachMailId;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			insertMailAuditLog(organizationId, action, mailToAddressString, mailSentHeader, mailSubject, mailBody,
					null);
		}

	}
}