package com.prutech.mailsender.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "organizationId", "action" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailTemplate extends BaseEntity implements IGenericEntity {

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = -5407560281608553914L;

	//
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "mail_template_id")
	private String mailTemplateId;

	//
	@NotNull
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
	private String organizationId;

	//
	@NotNull
	@Size(min = 1, max = 255, message = " you must use between 1 and 255 characters")
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

	//
	private String mailFrom;

	//
	private String mailCc;

	//
	private String mailBcc;

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
