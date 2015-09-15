/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.service;

import java.util.List;

import javax.mail.URLName;

public interface DNSResolver {

  public static final String SMTPScheme = "smtp://";
  
  public List<URLName> getMXRecordsForHost(String hostName); 

}
