/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.service;

import java.util.Collection;

import javax.mail.Message.RecipientType;
import javax.mail.URLName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmtpServerImpl implements SmtpServer {

	private static final Logger logger = LoggerFactory.getLogger(SmtpServerImpl.class);

	private static final int DEFAULT_PORT = 25;

	private final String senderHost;
	private final DNSResolver resolver = new DNSResolverImpl();

	public SmtpServerImpl(String senderHost) {
		this.senderHost = senderHost;
	}

	private static String getHost(String email) {
		if (email != null) {
			int n = email.indexOf("@");
			if (n == -1) {
				return null;
			}
			return StringUtils.trimToNull(email.substring(n + 1));
		}
		return null;
	}

	@Override
	public DeliveryResult send(SmtpMessage message) {

		DeliveryResult result = new DeliveryResult();
		
		String host = getHost(message.getToAddress());
		if (host == null) {
			result.setCode(DeliveryCode.INVALID_TO_ADDRESS);
			return result;
		}
		result.setDeliveryHost(host);
		
		Collection<URLName> mxs = resolver.getMXRecordsForHost(host);
		if (CollectionUtils.isEmpty(mxs)) {
			result.setCode(DeliveryCode.MX_RECORDS_NOT_FOUND);
			return result;
		}
		
		Email email = new Email();
		email.setFromAddress(message.getFromName(), message.getFromAddress());
		email.addRecipient(message.getToName(), message.getToAddress(),
				RecipientType.TO);
		
		email.setText(message.getBody());
		
		if (message.getBodyHTML() == null) {
			email.setTextHTML(StringUtils.replace(message.getBody(), "\n",
					"<br/>", -1));
		} else {
			email.setTextHTML(message.getBodyHTML());
		}
		
		email.setSubject(message.getSubject());
		
		for (URLName mx : mxs) {
			try {

				Mailer mailer = new Mailer(mx.getHost(), DEFAULT_PORT, null,
						null, TransportStrategy.SMTP_PLAIN);

				mailer.sendMail(email);

				result.setMxHost(mx.getHost());
				result.setCode(DeliveryCode.OK);
				result.setMessage(null);
				
			} catch (MailException e) {
				logger.error("send mail on " + mx.getHost(), e);
				result.setCode(DeliveryCode.TRASPORT_ERROR);
				result.setMessage(e.getMessage());
			}
		}
		
		return result;
	}


}
