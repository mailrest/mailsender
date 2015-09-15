/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.repository;

import com.mailrest.maildal.config.MailDalConfig;
import com.mailrest.maildal.config.RepositoryConfig;
import com.mailrest.maildal.repository.MessageQueueRepository;

public final class MessageQueueRepositoryImpl extends RepositoryConfig implements MessageQueueRepository {
	
	public MessageQueueRepositoryImpl(MailDalConfig dalConfig) {
		super(dalConfig);
	}
	
}
