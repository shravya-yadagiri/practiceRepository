package com.prutech.mailsender.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prutech.mailsender.model.MailAuditLog;

@Repository
public interface MailAuditLogRepository extends JpaRepository<MailAuditLog, String> {

}
