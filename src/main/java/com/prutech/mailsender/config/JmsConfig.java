/**
 * 
 */
package com.prutech.mailsender.config;

import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author venkat.sai
 *
 */
@EnableJms
@Configuration
public class JmsConfig {
	
	@Value("${activemq.broker-url}")
    private String brokerUrl;
	
	@Value("${activemq.mailsender-queue}")
    private String mailSenderQueue;
	
	@Value("${activemq.recoverymails-queue}")
    private String recoveryMailsQueue;

    @Bean
    public Queue mailSenderQueue() {
        return new ActiveMQQueue(mailSenderQueue);
    }
    
    @Bean
    public Queue recoveryMailsQueue() {
        return new ActiveMQQueue(recoveryMailsQueue);
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        factory.setTrustAllPackages(true);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(activeMQConnectionFactory());
    }
	

}
