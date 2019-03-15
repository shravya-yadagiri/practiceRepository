package com.prutech.mailsender.model;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Created by @author Venkat Giri 11-Feb-2019
 *
 * ResponseData.java
 *
 * Copyright 2018 Prutech Solutions Pvt Ltd., India. All rights reserved.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {
	
	//
	@NotEmpty
	String key;
	
	//
	@NotEmpty
	String value;
}
