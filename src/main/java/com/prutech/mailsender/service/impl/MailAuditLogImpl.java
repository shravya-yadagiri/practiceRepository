package com.prutech.mailsender.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.prutech.mailsender.dao.MailAuditLogRepository;
import com.prutech.mailsender.model.MailAuditLog;
import com.prutech.mailsender.model.ResponseData;
import com.prutech.mailsender.service.MailAuditLogService;

@Service
public class MailAuditLogImpl implements MailAuditLogService {

	@Autowired
	private MailAuditLogRepository mailAuditLogRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public MailAuditLog saveMailAuditLog(MailAuditLog mailAuditLog) {
		mailAuditLog.setCreatedDate(new Date());
		mailAuditLogRepository.save(mailAuditLog);
		mailAuditLog.appendData(new ResponseData("status", "success"));
		return mailAuditLog;
	}
}
