/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.service;

public interface SmtpServer {

  public DeliveryResult send(SmtpMessage message);
  
}
