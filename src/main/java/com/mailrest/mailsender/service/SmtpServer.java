package com.mailrest.mailsender.service;

public interface SmtpServer {

  public SmtpServerStatus send(SmtpMessage message);
  
}
