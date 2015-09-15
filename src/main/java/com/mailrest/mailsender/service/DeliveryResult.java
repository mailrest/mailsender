package com.mailrest.mailsender.service;

import com.mailrest.maildal.model.MessageAction;
import com.mailrest.maildal.model.MessageDelivery;

public final class DeliveryResult implements MessageDelivery {

	private String deliveryHost;
	private boolean tls;
	private boolean certificateVerified;
	private String mxHost;
	private double sessionSeconds;
	private DeliveryCode code;
	private String message;

	public DeliveryResult() {
	}

	public static DeliveryResult of(DeliveryCode code) {
		DeliveryResult result = new DeliveryResult();
		result.setCode(code);
		return result;
	}
	
	public void setDeliveryHost(String deliveryHost) {
		this.deliveryHost = deliveryHost;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
	}

	public void setCertificateVerified(boolean certificateVerified) {
		this.certificateVerified = certificateVerified;
	}

	public void setMxHost(String mxHost) {
		this.mxHost = mxHost;
	}

	public void setSessionSeconds(double sessionSeconds) {
		this.sessionSeconds = sessionSeconds;
	}

	public void setCode(DeliveryCode code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String deliveryHost() {
		return deliveryHost;
	}

	@Override
	public boolean tls() {
		return tls;
	}

	@Override
	public boolean certificateVerified() {
		return certificateVerified;
	}

	@Override
	public String mxHost() {
		return mxHost;
	}

	@Override
	public double sessionSeconds() {
		return sessionSeconds;
	}

	@Override
	public int code() {
		return code.getCode();
	}

	@Override
	public String description() {
		return code.name();
	}

	@Override
	public String message() {
		return message;
	}

	public MessageAction toMessageAction() {
		switch(code) {
		case OK:
			return MessageAction.DELIVERED;
		case TRASPORT_ERROR:
			return MessageAction.REJECTED;
		default:
			return MessageAction.FAILED;	
		}
	}
	
	public boolean isDelivered() {
		return code == DeliveryCode.OK;
	}
	
}
