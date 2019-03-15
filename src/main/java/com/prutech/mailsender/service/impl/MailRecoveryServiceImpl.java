package com.prutech.mailsender.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.prutech.mailsender.dao.MailRecoveryRepository;
import com.prutech.mailsender.model.MailRecovery;
import com.prutech.mailsender.model.ResponseData;
import com.prutech.mailsender.service.MailRecoveryService;

@Service
public class MailRecoveryServiceImpl implements MailRecoveryService {

	@Autowired
	private MailRecoveryRepository mailRecoveryRepository;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Queue recoveryMailsQueue;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailRecovery saveMailRecovery(MailRecovery mailRecovery) {
		mailRecovery.setCreatedDate(new Date());
		mailRecoveryRepository.save(mailRecovery);
		mailRecovery.appendData(new ResponseData("status", "success"));
		return mailRecovery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.mailsender.service.MailRecoveryService#
	 * saveAllRecoveryMailsIntoActiveMQ()
	 */
	@Override
	@Transactional
	public void saveAllRecoveryMailsIntoActiveMQ() {
		List<MailRecovery> allMailRecovery = mailRecoveryRepository.findAll();

		for (MailRecovery eachMailRecovery : allMailRecovery) {
			jmsTemplate.convertAndSend(recoveryMailsQueue, eachMailRecovery);
		}
		System.out.println(
				"MailRecoveryServiceImpl.saveAllRecoveryMailsIntoActiveMQ()...added all recovery mails to Active MQ");

		mailRecoveryRepository.deleteAll();
		System.out.println("MailRecoveryServiceImpl.saveAllRecoveryMailsIntoActiveMQ()...deleted all recovery mails");

	}
}
