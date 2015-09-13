package com.mailrest.mailsender;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mailrest.maildal.config.MailDalConfig;
import com.mailrest.maildal.model.MessageQueue;
import com.mailrest.maildal.repository.MessageQueueRepository;

public class SenderDaemon {

	private static final Logger logger = LoggerFactory
			.getLogger(SenderDaemon.class);
	
	private final int bucketId;
	private final long sleepMls;
	private final MailDalConfig dalConfig;
	
	private final MessageQueueRepository messageQueueRepository;

	
	public SenderDaemon(SenderConfig config) {
		
		this.bucketId = config.getBucketId();
		this.sleepMls = ((long) config.getPullIntervalSec()) * 1000;
		
		this.dalConfig = new MailDalConfig(config.getCassandraHost(), config.getCassandraKeyspace());
	
		this.messageQueueRepository = new MessageQueueRepositoryImpl(dalConfig);
		
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
				logger.error("alreadyPeekedMessageId = " + msgQueue.messageId());
			}
			
			messageQueueRepository.peekMessageCommit(msgQueue);
			
			trySendMessage(msgQueue);
			
			messageQueueRepository.pollMessage(msgQueue);
			
		}
		
	}
	
	
	private void sleep() {
		try {
			Thread.currentThread().sleep(sleepMls);
		} catch (InterruptedException e) {
			return;
		}
	}
	
	private void trySendMessage(MessageQueue msgQueue) {
		
		System.out.println("Send message " + msgQueue.messageId());
		
	}
	
	
	public static void main(String[] args) {
		PidFileCleaner.deletePidFileOnExit();

		SenderConfig config = new SenderConfig();

		System.out.println("MailSender daemon started by " + config.getUser() + " on " + config.getHost());

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
