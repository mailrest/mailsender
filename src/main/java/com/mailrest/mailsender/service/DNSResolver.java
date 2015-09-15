package com.mailrest.mailsender.service;

import java.util.Collection;

import javax.mail.URLName;

public interface DNSResolver {

  public static final String SMTPScheme = "smtp://";
  
  public Collection<URLName> getMXRecordsForHost(String hostName); 

}
