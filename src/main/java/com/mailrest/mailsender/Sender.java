package com.mailrest.mailsender;

import com.mailrest.maildal.model.MessageSender;

public final class Sender implements MessageSender {

	private final String senderName;
	private final String senderIp;
	
	public Sender(SenderConfig config) {
		this.senderName = config.getJvmId();
		this.senderIp = config.getExternalIp();
	}
	
	@Override
	public String senderName() {
		return senderName;
	}

	@Override
	public String senderIp() {
		return senderIp;
	}

}
