package com.prutech.mailsender.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prutech.mailsender.model.MailRecovery;

@Repository
public interface MailRecoveryRepository extends JpaRepository<MailRecovery, String> {

}
