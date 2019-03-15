package com.prutech.mailsender.util;

import java.io.StringReader;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.prutech.mailsender.model.MailServerDetails;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 
 * @author venkat.sai
 *
 */
public class MailSenderUtil {

	public static ConcurrentHashMap<String, JavaMailSenderImpl> mailSenderMaps = new ConcurrentHashMap<>();

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
}
