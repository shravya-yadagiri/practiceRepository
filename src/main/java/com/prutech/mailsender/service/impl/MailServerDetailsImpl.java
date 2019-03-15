package com.prutech.mailsender.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.prutech.mailsender.dao.MailServerDetailsRepository;
import com.prutech.mailsender.model.MailServerDetails;
import com.prutech.mailsender.model.ResponseData;
import com.prutech.mailsender.model.StatusEnum;
import com.prutech.mailsender.service.MailServerDetailsService;
import com.prutech.mailsender.util.PasswordEncryptionUtil;

@Service
public class MailServerDetailsImpl implements MailServerDetailsService {

	@Autowired
	private MailServerDetailsRepository mailServerDetailsRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailServerDetails saveMailServerDetails(MailServerDetails mailServerDetails) {
		if (mailServerDetails.getMailPassword() != null) {
			mailServerDetails.setMailPassword(PasswordEncryptionUtil.encrypt(mailServerDetails.getMailPassword(),
					mailServerDetails.getOrganizationId()));
		}
		mailServerDetails.setStatus(StatusEnum.ACTIVE.getStatusCode());
		mailServerDetails.setCreatedDate(new Date());
		mailServerDetailsRepository.save(mailServerDetails);
		mailServerDetails.appendData(new ResponseData("status", "success"));
		return mailServerDetails;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailServerDetails updateMailServerDetails(MailServerDetails mailServerDetails) {
		Optional<MailServerDetails> mailServerDetailsOptional = mailServerDetailsRepository
				.findById(mailServerDetails.getMailServerDetailsId());
		if (mailServerDetailsOptional.isPresent()) {
			mailServerDetailsRepository.save(mailServerDetails);
			mailServerDetails.appendData(new ResponseData("status", "success"));
		} else {
			mailServerDetails.appendData(new ResponseData("status", "failed"));
		}
		return mailServerDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.mailsender.service.MailServerDetailsService#
	 * getMailServerDetailsById(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Optional<MailServerDetails> getMailServerDetailsById(String mailServerDetailsId) {
		return mailServerDetailsRepository.findById(mailServerDetailsId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailServerDetails deleteMailServerDetails(MailServerDetails mailServerDetails) {
		Optional<MailServerDetails> mailServerDetailsOptional = mailServerDetailsRepository
				.findById(mailServerDetails.getMailServerDetailsId());
		if (mailServerDetailsOptional.isPresent()) {
			mailServerDetails.setStatus(StatusEnum.INACTIVE.getStatusCode());
			mailServerDetailsRepository.save(mailServerDetails);
			mailServerDetails.appendData(new ResponseData("status", "success"));
		} else {
			mailServerDetails.appendData(new ResponseData("status", "failed"));
		}
		return mailServerDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.mailsender.service.MailServerDetailsService#
	 * getAllMailServerDetails()
	 */
	@Override
	public List<MailServerDetails> getAllActiveMailServerDetails() {
		return mailServerDetailsRepository.findByStatus(StatusEnum.ACTIVE.getStatusCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.mailsender.service.MailServerDetailsService#
	 * getMailServerDetailsByOrganizationId(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Optional<MailServerDetails> getMailServerDetailsByOrganizationId(String organizationId) {
		return mailServerDetailsRepository.findByOrganizationId(organizationId);
	}
}
