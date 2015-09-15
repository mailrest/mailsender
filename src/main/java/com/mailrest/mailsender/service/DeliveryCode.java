/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.service;

public enum DeliveryCode {
	
	OK(200), 
	INVALID_FROM_ADDRESS(401),
	INVALID_TO_ADDRESS(402), 
	MX_RECORDS_NOT_FOUND(403),
	USER_NOT_FOUND(404), 
	TRASPORT_ERROR(500),
	MESSAGE_NOT_FOUND(501);

	private int code;

	private DeliveryCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
