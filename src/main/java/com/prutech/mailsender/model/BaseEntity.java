package com.prutech.mailsender.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author venkat.sai
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {

	//
	@Column
	private String createdBy;

	//
	@Column(insertable = true, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Date createdDate;

	//
	@Column
	private String lastModifiedBy;

	//
	@Temporal(TemporalType.TIMESTAMP)
	@Column(insertable = true, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Date lastModifiedDate;

	//
	@Version
	private long version;

	//
	@Column(columnDefinition = "TINYINT", nullable = false)
	private int status;
}
