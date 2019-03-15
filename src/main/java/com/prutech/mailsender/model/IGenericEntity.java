package com.prutech.mailsender.model;

import java.io.Serializable;

/**
 * 
 * @author venkat.sai
 *
 */
public interface IGenericEntity extends Serializable{

	/**
	 * 
	 * @param data
	 */
	public void appendData(ResponseData data);
}
