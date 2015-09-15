/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.repository;

import com.mailrest.maildal.config.MailDalConfig;
import com.mailrest.maildal.config.RepositoryConfig;
import com.mailrest.maildal.repository.MessageLogRepository;

public final class MessageLogRepositoryImpl extends RepositoryConfig implements MessageLogRepository {

	public MessageLogRepositoryImpl(MailDalConfig dalConfig) {
		super(dalConfig);
	}
	
}
