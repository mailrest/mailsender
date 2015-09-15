package com.mailrest.mailsender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mailrest.mailsender.service.SmtpMessage;
import com.mailrest.mailsender.service.SmtpServer;
import com.mailrest.mailsender.service.SmtpServerImpl;
import com.mailrest.mailsender.service.SmtpServerStatus;
import com.mailrest.mailsender.support.DoubleShortter;

public class SendMailApp {

	private static final String SYS_FROM = System.getProperty("from");
	private static final String SYS_TO = System.getProperty("to");
	private static final String SYS_SUBJECT = System.getProperty("subject");
	private static final String SYS_BODY = System.getProperty("body");
	private static final String SYS_SHOW_MIME = System.getProperty("showMime");
	
	private SmtpServer smtpServer = new SmtpServerImpl();
	
	public static void main(String[] args) {
		
		try {
			new SendMailApp().run();
		}
		catch(Exception e) {
			e.printStackTrace(System.err);
		}
		
		
	}
	
	private void run() throws IOException {

		SenderConfig config = new SenderConfig();
		System.out.println("sendmail started by " + config.getUser() + " on " + config.getHost());
		
		SmtpMessage msg = new SmtpMessage();
		
		String from = read(SYS_FROM, "Enter from:");
		String to = read(SYS_TO, "Enter to:");
		String subject = read(SYS_SUBJECT, "Enter subject:");
		String body = read(SYS_BODY, "Enter body:");

		msg.setFromAddress(from);
		msg.setToAddress(to);
		msg.setSubject(subject);
		msg.setBody(body);
		
		long time0 = System.currentTimeMillis();
		SmtpServerStatus status = smtpServer.send(msg);
		long time1 = System.currentTimeMillis();
		
		double seconds = ((double) (time1 - time0)) / 1000;
		
		System.out.println("status = " + status + ", seconds = " + DoubleShortter.shortter(seconds));
		
	}

	private static String read(String value, String message) throws IOException {
		
		if (value != null) {
			return value;
		}
		
		System.out.println(message);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		return reader.readLine();
		
	}
	
}
