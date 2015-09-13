package com.mailrest.mailsender;

import com.mailrest.maildal.config.MailDalConfig;
import com.mailrest.maildal.config.RepositoryConfig;
import com.mailrest.maildal.repository.MessageQueueRepository;

public final class MessageQueueRepositoryImpl extends RepositoryConfig implements MessageQueueRepository {
	
	public MessageQueueRepositoryImpl(MailDalConfig dalConfig) {
		super(dalConfig);
	}
	
}
