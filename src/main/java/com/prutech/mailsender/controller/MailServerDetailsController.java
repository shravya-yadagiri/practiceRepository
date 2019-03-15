package com.prutech.mailsender.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prutech.mailsender.model.MailServerDetails;
import com.prutech.mailsender.service.MailServerDetailsService;

@RestController
@RequestMapping("/api/v1/mailServerDetails")
public class MailServerDetailsController {

	@Autowired
	private MailServerDetailsService mailServerDetailsService;

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public MailServerDetails saveMailServerDetails(@RequestBody MailServerDetails mailServerDetails) {
		return mailServerDetailsService.saveMailServerDetails(mailServerDetails);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public MailServerDetails updateMailServerDetails(@RequestBody MailServerDetails mailServerDetails) {
		return mailServerDetailsService.updateMailServerDetails(mailServerDetails);
	}

	@RequestMapping(value = "/get/{mailServerDetailsId}", method = RequestMethod.GET)
	public Optional<MailServerDetails> getMailServerDetailsById(@PathVariable String mailServerDetailsId) {
		return mailServerDetailsService.getMailServerDetailsById(mailServerDetailsId);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, consumes = {
			MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE })
	public MailServerDetails deleteMailServerDetails(@RequestBody MailServerDetails mailServerDetails) {
		return mailServerDetailsService.deleteMailServerDetails(mailServerDetails);
	}

}
