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
public class MailAuditLog extends BaseEntity implements IGenericEntity {

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = 4635666960825765012L;

	//
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "mail_audit_log_id")
	private String mailAuditLogId;

	//
	@NotNull
	private String organizationId;

	//
	@NotNull
	private String action;

	//
	@NotNull
	@Column(columnDefinition = "TINYTEXT CHARACTER SET utf8mb4")
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String mailSubject;

	//
	@NotNull
	@Column(columnDefinition = "MEDIUMTEXT CHARACTER SET utf8mb4")
	@Size(min = 1, max = 10000, message = " you must use between 1 and 10000 characters")
	private String mailBody;

	/*//
	private String mailFrom;*/

	//
	private String mailToAddress;

	/*//
	private String mailCc;

	//
	private String mailBcc;*/
	
	//
	@Column(columnDefinition = "MEDIUMTEXT CHARACTER SET utf8mb4")
	@Size(min = 1, max = 10000, message = " you must use between 1 and 10000 characters")
	private String mailSentHeader;
	
	//
	private String comments;

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
