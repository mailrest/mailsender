/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender;

public class StatusApp {

	public static void main(String[] args) {

		SenderConfig config = new SenderConfig();
		
		System.out.println("MailSender status started by " + config.getUser() + " on " + config.getHost());

		
		
	}

}
