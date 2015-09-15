package com.mailrest.mailsender.service;

import java.net.InetAddress;
import java.util.Collection;

import javax.mail.Message.RecipientType;
import javax.mail.URLName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;

public class SmtpServerImpl implements SmtpServer {

  private static final Log log = LogFactory.getLog(SmtpServerImpl.class);
  
  private static final int DEFAULT_PORT = 25;
  
  private String charset = "UTF-8";//"KOI8-R";
  private String localhost = "mailrest.com";
  private DNSResolver resolver = new DNSResolverImpl();
  private String replaceTo = null;
  
  public SmtpServerImpl() {
    try {
      localhost = InetAddress.getLocalHost().getCanonicalHostName();
      if (localhost.indexOf(".") == -1) {
        // we need to have full host name
        localhost = localhost + ".com";
      }
    }
    catch(Exception e) {
      log.error("fail get localhost", e);
    }
  }
  
  private static String getHost(String email) {
    if (email != null) {
      int n = email.indexOf("@");
      if (n == -1) {
        return null;
      }
      return StringUtils.trimToNull(email.substring(n+1));
    }
    return null;
  }
  
  @Override
  public SmtpServerStatus send(SmtpMessage message) {
    if (replaceTo != null) {
      message.setToAddress(replaceTo);
    }
    String host = getHost(message.getToAddress());
    if (host == null) {
      return SmtpServerStatus.INVALID_ADDRESS;
    }
    Collection<URLName> mxs = resolver.getMXRecordsForHost(host);
    if (CollectionUtils.isEmpty(mxs)) {
      return SmtpServerStatus.INVALID_ADDRESS;
    }
    SmtpServerStatus lastError = SmtpServerStatus.INVALID_ADDRESS;
    Email email = new Email();
    email.setFromAddress(message.getFromName(), message.getFromAddress());
    email.addRecipient(message.getToName(), message.getToAddress(), RecipientType.TO);
    email.setText(message.getBody());
    if (message.getBodyHTML() == null) {
      email.setTextHTML(StringUtils.replace(message.getBody(), "\n", "<br/>", -1));
    }
    else {
      email.setTextHTML(message.getBodyHTML());
    }
    email.setSubject(message.getSubject());
    for (URLName mx : mxs) {
      try {
    	  
    	  Mailer mailer = new Mailer(mx.getHost(), DEFAULT_PORT, null, null,
    				TransportStrategy.SMTP_PLAIN);

    	  mailer.sendMail(email);
        
        return SmtpServerStatus.OK;
      }
      catch(MailException e) {
        log.info("send mail", e);
        lastError = SmtpServerStatus.TRASPORT_ERROR;
      }
    }
    return lastError;
  }

  public DNSResolver getResolver() {
    return resolver;
  }

  public void setResolver(DNSResolver resolver) {
    this.resolver = resolver;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charsetName) {
    this.charset = charsetName;
  }

  public String getLocalhost() {
    return localhost;
  }

  public void setLocalhost(String localhost) {
    this.localhost = localhost;
  }

  public String getReplaceTo() {
    return replaceTo;
  }

  public void setReplaceTo(String replaceTo) {
    this.replaceTo = StringUtils.trimToNull(replaceTo);
  }

}
