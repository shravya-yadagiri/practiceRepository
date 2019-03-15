package com.prutech.mailsender.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.prutech.mailsender.model.MailTemplate;
import com.prutech.mailsender.service.MailTemplateService;

@RestController
@RequestMapping("/api/v1/template")
public class TemplateController {

	@Autowired
	private MailTemplateService mailTemplateService;

	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public MailTemplate saveTemplate(@RequestBody MailTemplate mailTemplate) {
		return mailTemplateService.saveTemplate(mailTemplate);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_UTF8_VALUE,
			MediaType.APPLICATION_XML_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE,
					MediaType.APPLICATION_XML_VALUE })
	public MailTemplate updateTemplate(@RequestBody MailTemplate mailTemplate) {
		return mailTemplateService.updateTemplate(mailTemplate);
	}

	@RequestMapping(value = "/get/{templateId}", method = RequestMethod.GET)
	public Optional<MailTemplate> getTemplateById(@PathVariable String templateId) {
		return mailTemplateService.getTemplateById(templateId);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE, consumes = {
			MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE })
	public MailTemplate deleteTemplate(@RequestBody MailTemplate mailTemplate) {
		return mailTemplateService.deleteTemplate(mailTemplate);
	}

}
