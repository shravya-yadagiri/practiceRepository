package com.prutech.mailsender.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prutech.mailsender.model.MailServerDetails;

@Repository
public interface MailServerDetailsRepository extends JpaRepository<MailServerDetails, String> {

	Optional<MailServerDetails> findByOrganizationId(String organizationId);

	/**
	 * @return
	 */
	List<MailServerDetails> findByStatus(int status);

}
