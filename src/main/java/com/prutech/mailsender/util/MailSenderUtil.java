package com.prutech.mailsender.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prutech.mailsender.dao.MailTemplateRepository;
import com.prutech.mailsender.model.MailServerDetails;
import com.prutech.mailsender.model.MailTemplate;
import com.prutech.mailsender.model.StatusEnum;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author venkat.sai
 *
 */
public class MailSenderUtil {

	public static ConcurrentHashMap<String, MailServerDetails> mailServerDetails = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, JavaMailSenderImpl> mailSenders = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Date> mailServerDetailsLastModifiedDates = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<String, MailTemplate> templatesMap = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, Date> templatesLastModifiedDates = new ConcurrentHashMap<>();

	public static JavaMailSenderImpl createMailSender(MailServerDetails mailServerDetails) {

		System.out.println("MailSenderUtil.createMailSender()...mailServerDetails:" + mailServerDetails);
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		if (mailServerDetails.getMailHost() != null) {
			mailSender.setHost(mailServerDetails.getMailHost());
		}

		if (mailServerDetails.getMailPort() != null) {
			mailSender.setPort(Integer.valueOf(mailServerDetails.getMailPort()));
		}

		if (mailServerDetails.getMailUsername() != null) {
			mailSender.setUsername(mailServerDetails.getMailUsername());
		}

		if (mailServerDetails.getMailPassword() != null) {
			mailSender.setPassword(PasswordEncryptionUtil.decrypt(mailServerDetails.getMailPassword(),
					mailServerDetails.getOrganizationId()));
		}

		Properties mailSenderProperties = mailSender.getJavaMailProperties();

		if (mailServerDetails.getMailSmtpAuth() != null) {
			mailSenderProperties.put("mail.smtp.auth", mailServerDetails.getMailSmtpAuth());
		} else {
			mailSenderProperties.put("mail.smtp.auth", "false");
		}

		if (mailServerDetails.getMailDebug() != null) {
			mailSenderProperties.put("mail.debug", mailServerDetails.getMailDebug());
		}

		if (mailServerDetails.getMailSmtpConnectiontimeout() != null) {
			mailSenderProperties.put("mail.smtp.connectiontimeout", mailServerDetails.getMailSmtpConnectiontimeout());
		}

		if (mailServerDetails.getMailSmtpTimeout() != null) {
			mailSenderProperties.put("mail.smtp.timeout", mailServerDetails.getMailSmtpTimeout());
		}

		if (mailServerDetails.getMailSmtpWritetimeout() != null) {
			mailSenderProperties.put("mail.smtp.writetimeout", mailServerDetails.getMailSmtpWritetimeout());
		}

		if (mailServerDetails.getMailSmtpSslTrust() != null) {
			mailSenderProperties.put("mail.smtp.ssl.trust", mailServerDetails.getMailSmtpSslTrust());
		}

		if (mailServerDetails.getMailFromMailId() != null) {
			mailSenderProperties.put("mail.from.mail.id", mailServerDetails.getMailFromMailId());
		}

		return mailSender;
	}

	public static String decodeStringUsingBase64(String encodedString) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedByte = decoder.decode(encodedString);
		return new String(decodedByte);
	}

	public static String encodeStringUsingBase64(String decodedString) {
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(decodedString.getBytes());
	}

	public static String getReplacedHtmlContent(String templateContent, Map<String, Object> model) throws Exception {
		System.out.println("MailSenderUtil.getReplacedHtmlWithModel()...templateContent:" + templateContent);
		String decodedString = decodeStringUsingBase64(templateContent);
		System.out.println("MailSenderServiceImpl.sendMail()...decodedString:" + decodedString);
		Template freeMarkerTemplateForBody = new Template("name", new StringReader(decodedString), new Configuration());

		String replacedHtmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerTemplateForBody,
				model);

		return replacedHtmlContent;
	}

	public static void storeAllOrganizationTemplates(String organizationId, ApplicationContext applicationContext) {
		System.out.println("MailSenderUtil.storeAllOrganizationTemplates()...organizationId:" + organizationId);

		MailTemplateRepository mailTemplateRepository = applicationContext.getBean(MailTemplateRepository.class);
		System.out.println(
				"MailSenderUtil.storeAllOrganizationTemplates()...mailTemplateRepository:" + mailTemplateRepository);
		List<MailTemplate> allOrganizationTemplates = mailTemplateRepository
				.findByOrganizationIdAndStatus(organizationId, StatusEnum.ACTIVE.getStatusCode());
		for (MailTemplate eachActiveTemplate : allOrganizationTemplates) {

			templatesMap.put(organizationId + "|" + eachActiveTemplate.getAction(), eachActiveTemplate);
			templatesLastModifiedDates.put(organizationId + "|" + eachActiveTemplate.getAction(),
					eachActiveTemplate.getLastModifiedDate());
		}
	}

	public static String convertObjectToJson(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}

	public static Object convertJsonToObject(String string) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(string, Object.class);
	}
}
