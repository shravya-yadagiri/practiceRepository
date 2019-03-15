package com.prutech.mailsender.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailServerDetails extends BaseEntity implements IGenericEntity {

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = 5077507426337489372L;

	//
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "mail_server_details_id")
	private String mailServerDetailsId;

	//
	@NotNull
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	@Column(unique = true)
	private String organizationId;

	//
	@NotNull
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String mailHost;

	//
	@NotNull
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String mailPort;

	//
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String mailUsername;

	//
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String mailPassword;

	//
	@NotNull
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String mailSmtpAuth;

	//
	private String mailDebug;

	//
	private String mailSmtpConnectiontimeout;

	//
	private String mailSmtpTimeout;

	//
	private String mailSmtpWritetimeout;

	//
	private String mailSmtpSslTrust;

	//
	private String mailFromMailId;

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
