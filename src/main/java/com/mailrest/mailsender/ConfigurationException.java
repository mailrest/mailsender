package com.mailrest.mailsender;

public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = -6868896696902534468L;

	public ConfigurationException(String message) {
		super(message);
	}
	
	public ConfigurationException(String message, Throwable t) {
		super(message, t);
	}
	
}
