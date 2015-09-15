/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.repository;

import com.mailrest.maildal.config.MailDalConfig;
import com.mailrest.maildal.config.RepositoryConfig;
import com.mailrest.maildal.repository.MessageRepository;

public final class MessageRepositoryImpl extends RepositoryConfig implements MessageRepository {

	public MessageRepositoryImpl(MailDalConfig dalConfig) {
		super(dalConfig);
	}
	
}
