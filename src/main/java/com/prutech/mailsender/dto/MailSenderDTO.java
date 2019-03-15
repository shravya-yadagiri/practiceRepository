package com.prutech.mailsender.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.prutech.mailsender.model.IGenericEntity;
import com.prutech.mailsender.model.ResponseData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSenderDTO implements IGenericEntity {

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = 5077507426337489372L;

	//
	private String organizationId;

	//
	private String action;

	//
	private Map<String, Object> model;

	//
	@Transient
	List<ResponseData> responseData = new ArrayList<ResponseData>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.prutech.boot.IGenericEntity#appendData(com.prutech.boot.model.
	 * ResponseData)
	 */
	public void appendData(ResponseData data) {
		responseData.add(data);
	}

}
