package com.prutech.mailsender.model;

public enum StatusEnum {
	
	INACTIVE(0), ACTIVE(1);
	
	private int statusCode;

	private StatusEnum(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public static StatusEnum fromName(String name) {
		return valueOf(name.toUpperCase());
	}

}