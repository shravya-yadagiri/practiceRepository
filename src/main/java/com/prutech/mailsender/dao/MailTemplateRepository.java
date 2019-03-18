package com.prutech.mailsender.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prutech.mailsender.model.MailTemplate;

@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate, String> {

	Optional<MailTemplate> findByActionAndOrganizationId(String action, String organizationId);
	
	List<MailTemplate> findByOrganizationIdAndStatus(String organizationId, int status);

}
