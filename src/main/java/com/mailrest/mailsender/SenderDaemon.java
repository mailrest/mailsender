/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mailrest.maildal.config.MailDalConfig;
import com.mailrest.maildal.model.Message;
import com.mailrest.maildal.model.MessageQueue;
import com.mailrest.maildal.model.MessageRecipient;
import com.mailrest.maildal.repository.MessageLogRepository;
import com.mailrest.maildal.repository.MessageQueueRepository;
import com.mailrest.maildal.repository.MessageRepository;
import com.mailrest.maildal.support.Recipient;
import com.mailrest.maildal.support.Recipients;
import com.mailrest.mailsender.repository.MessageLogRepositoryImpl;
import com.mailrest.mailsender.repository.MessageQueueRepositoryImpl;
import com.mailrest.mailsender.repository.MessageRepositoryImpl;
import com.mailrest.mailsender.service.DeliveryCode;
import com.mailrest.mailsender.service.DeliveryResult;
import com.mailrest.mailsender.service.SmtpMessage;
import com.mailrest.mailsender.service.SmtpServer;
import com.mailrest.mailsender.service.SmtpServerImpl;
import com.mailrest.mailsender.support.DoubleShortter;

public class SenderDaemon {

	private static final Logger logger = LoggerFactory
			.getLogger(SenderDaemon.class);
	
	private final SenderConfig config;
	private final int bucketId;
	private final long sleepMls;
	private final MailDalConfig dalConfig;
	
	private final MessageQueueRepository messageQueueRepository;
	private final MessageRepository messageRepository;
	private final MessageLogRepository messageLogRepository;

	private final SmtpServer smtpServer;
	
	private final Sender sender;
	
	public SenderDaemon(SenderConfig config) {
		
		this.config = config;
		this.bucketId = config.getBucketId();
		this.sleepMls = ((long) config.getPullIntervalSec()) * 1000;
		
		this.dalConfig = new MailDalConfig(config.getCassandraHost(), config.getCassandraKeyspace());
	
		this.messageQueueRepository = new MessageQueueRepositoryImpl(dalConfig);
		this.messageRepository = new MessageRepositoryImpl(dalConfig);
		this.messageLogRepository = new MessageLogRepositoryImpl(dalConfig);
		
		this.smtpServer = new SmtpServerImpl(config.getHost());
		
		this.sender = new Sender(config);
	}
	
	public void run() {
		
		while(!Thread.interrupted()) {
		
			Optional<MessageQueue> op = messageQueueRepository.peekMessage(bucketId);
		
			if (!op.isPresent()) {
				sleep();
				continue;
			}
		
			MessageQueue msgQueue = op.get();
			
			if (msgQueue.peeked()) {
				logger.error("alreadyPeekedMessageId = " + msgQueue.messageId() + ", repeating delivery");
			}
			
			messageQueueRepository.peekMessageCommit(msgQueue);
			
			DeliveryResult result = sendMessage(msgQueue);
			
			messageQueueRepository.pollMessage(msgQueue);
			
			if (!result.isDelivered() && msgQueue.attempt() + 1 < config.getRepeatMaxTries()) {
				Date deliveryAt = new Date(System.currentTimeMillis() + 1000L * config.getRepeatIntervalSec());
				messageQueueRepository.enqueueMessage(msgQueue, deliveryAt);
			}	
		}
		
	}
	
	
	private void sleep() {
		try {
			Thread.sleep(sleepMls);
		} catch (InterruptedException e) {
			return;
		}
	}
	
	private DeliveryResult sendMessage(MessageQueue msgQueue) {
		
		System.out.println("Send message " + msgQueue.messageId());
		
		Optional<Message> opMessage = messageRepository.getMessage(msgQueue.messageId());
		
		if (!opMessage.isPresent()) {
			return DeliveryResult.of(DeliveryCode.MESSAGE_NOT_FOUND);
		}
		
		Message message = opMessage.get();
		
		SmtpMessage smtpMessage = new SmtpMessage();
		
		Recipient from;
		try {
			from = Recipients.parseSingle(message.fromRecipient());
		}
		catch(IllegalArgumentException e) {
			return DeliveryResult.of(DeliveryCode.INVALID_FROM_ADDRESS);
		}
		
		if (from.getName().isPresent()) {
			smtpMessage.setFromName(from.getName().get());
		}
		
		smtpMessage.setFromAddress(from.getEmail());
		
		MessageRecipient to = msgQueue.recipient();
		
		if (to.recipientName() != null) {
			smtpMessage.setToName(to.recipientName());
		}
		
		smtpMessage.setToAddress(to.recipientEmail());
		
		smtpMessage.setSubject(message.subject());
		smtpMessage.setBody(message.textBody());
		
		if (message.htmlBody() != null) {
			smtpMessage.setBodyHTML(message.htmlBody());
		}
		
		long time0 = System.currentTimeMillis();
		
		DeliveryResult result = smtpServer.send(smtpMessage);
		
		long time1 = System.currentTimeMillis();
		
		double seconds = ((double) (time1 - time0)) / 1000;

		result.setSessionSeconds(DoubleShortter.shortter(seconds));
		
		long timestamp = System.currentTimeMillis();
		int dayAt = new Date(timestamp).getDay();
		
		messageLogRepository.logMessageDeliveryAttempt(message, msgQueue.recipient(), dayAt, timestamp, result.toMessageAction(), sender, result);
		
		return result;
	}
	
	public static void main(String[] args) {
		PidFileCleaner.deletePidFileOnExit();

		SenderConfig config = new SenderConfig();

		System.out.println("MailSender daemon started by " + config.getUser() + " on " + config.getHost() + " external_ip " + config.getExternalIp());

		System.out.println("interval = " + config.getPullIntervalSec());
		
		try {
			new SenderDaemon(config).run();
		}
		catch(RuntimeException e) {
			logger.error("daemon exited with error", e);
			PidFileCleaner.deletePidFile();
			System.exit(1);
		}
		
	}
	
}
