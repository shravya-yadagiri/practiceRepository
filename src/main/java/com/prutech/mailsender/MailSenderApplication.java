package com.prutech.mailsender;

import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.prutech.mailsender.model.MailServerDetails;
import com.prutech.mailsender.service.MailServerDetailsService;
import com.prutech.mailsender.util.MailSenderUtil;

@SpringBootApplication
public class MailSenderApplication {

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	private MailServerDetailsService mailServerDetailsService;

	public static void main(String[] args) {
		SpringApplication.run(MailSenderApplication.class, args);
	}

	@Bean(name = "threadPoolExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(50);
		executor.setThreadNamePrefix("MailSenderThreadPoolExecutor-");
		executor.initialize();
		return executor;
	}

	@PostConstruct
	public void createMailSendersForAllOrganizations() {
		List<MailServerDetails> allActiveMailServerDetails = mailServerDetailsService.getAllActiveMailServerDetails();
		System.out.println("MailSenderApplication.createMailSenderImpls()...allActiveMailServerDetails:"
				+ allActiveMailServerDetails);
		for (MailServerDetails mailServerDetails : allActiveMailServerDetails) {

			System.out.println(
					"MailSenderApplication.createMailSendersForAllOrganizations()...mailServerDetails.getCreatedDate():"
							+ mailServerDetails.getCreatedDate());
			System.out.println(
					"MailSenderApplication.createMailSendersForAllOrganizations()...mailServerDetails.getLastModifiedDate():"
							+ mailServerDetails.getLastModifiedDate());

			// Storing fresh MailServerDetails
			MailSenderUtil.mailServerDetails.put(mailServerDetails.getOrganizationId(), mailServerDetails);

			// Storing JavaMailSenderImpl's
			MailSenderUtil.mailSenders.put(mailServerDetails.getOrganizationId(),
					MailSenderUtil.createMailSender(mailServerDetails));

			System.out.println(
					"MailSenderApplication.createMailSendersForAllOrganizations()...mailServerDetails.getOrganizationId():"
							+ mailServerDetails.getOrganizationId());
			System.out.println(
					"MailSenderApplication.createMailSendersForAllOrganizations()...mailServerDetails.getOrganizationId():"
							+ mailServerDetails.getOrganizationId());

			// Storing last modified dates
			MailSenderUtil.mailServerDetailsLastModifiedDates.put(mailServerDetails.getOrganizationId(),
					mailServerDetails.getLastModifiedDate());

			MailSenderUtil.mailSenders.forEach((key, value) -> System.out
					.println("MailSenderApplication.createMailSenderImpls()...key = " + key + ", Value = " + value));

			// Need to store all organization action templates into collection
			MailSenderUtil.storeAllOrganizationTemplates(mailServerDetails.getOrganizationId(), applicationContext);

		}
	}

	/*
	 * @Bean SpringContextProvider springContextProvider() { return new
	 * SpringContextProvider(); }
	 */
}
