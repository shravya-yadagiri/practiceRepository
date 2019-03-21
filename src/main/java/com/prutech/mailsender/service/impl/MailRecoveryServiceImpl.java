package com.prutech.mailsender.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.prutech.mailsender.dao.MailRecoveryRepository;
import com.prutech.mailsender.dto.MailSenderDTO;
import com.prutech.mailsender.model.MailRecovery;
import com.prutech.mailsender.model.ResponseData;
import com.prutech.mailsender.service.MailRecoveryService;
import com.prutech.mailsender.util.MailSenderUtil;

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
	public void saveAllRecoveryMailsIntoActiveMQ() throws JsonParseException, JsonMappingException, IOException {
		List<MailRecovery> allMailRecoveries = mailRecoveryRepository.findAll();

		for (MailRecovery eachMailRecovery : allMailRecoveries) {

			MailSenderDTO mailSenderDTO = new MailSenderDTO();
			mailSenderDTO.setOrganizationId(eachMailRecovery.getOrganizationId());
			mailSenderDTO.setAction(eachMailRecovery.getAction());
			Map<String, Object> model = (Map<String, Object>) MailSenderUtil
					.convertJsonToObject(eachMailRecovery.getModel());
			System.out.println("MailRecoveryServiceImpl.saveAllRecoveryMailsIntoActiveMQ()...model:" + model);
			mailSenderDTO.setModel(model);
			jmsTemplate.convertAndSend(recoveryMailsQueue, mailSenderDTO);
		}
		System.out.println(
				"MailRecoveryServiceImpl.saveAllRecoveryMailsIntoActiveMQ()...added all recovery mails to Active MQ");

		mailRecoveryRepository.deleteAll();
		System.out.println("MailRecoveryServiceImpl.saveAllRecoveryMailsIntoActiveMQ()...deleted all recovery mails");

	}
}
