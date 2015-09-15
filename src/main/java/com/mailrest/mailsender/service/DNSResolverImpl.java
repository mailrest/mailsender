package com.mailrest.mailsender.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Vector;

import javax.mail.URLName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSResolverImpl implements DNSResolver {

  private static final Log log = LogFactory.getLog(DNSResolverImpl.class);
  
  public Collection<URLName> getMXRecordsForHost(String hostName) {
    
    Vector<URLName> recordsColl = null;
    try {
      boolean foundOriginalMX = true;
      Record[] records = new Lookup(hostName, Type.MX).run();
      
      /*
       * Sometimes we should send an email to a subdomain which does not 
       * have own MX record and MX server. At this point we should find an 
       * upper level domain and server where we can deliver our email.
       *  
       * Example: subA.subB.domain.name has not own MX record and 
       * subB.domain.name is the mail exchange master of the subA domain 
       * too.
       */
      if( records == null || records.length == 0 )
      {
        foundOriginalMX = false;
        String upperLevelHostName = hostName;
        while(    records == null &&
              upperLevelHostName.indexOf(".") != upperLevelHostName.lastIndexOf(".") &&
              upperLevelHostName.lastIndexOf(".") != -1
          )
        {
          upperLevelHostName = upperLevelHostName.substring(upperLevelHostName.indexOf(".")+1);
          records = new Lookup(upperLevelHostName, Type.MX).run();
        }
      }

            if( records != null )
            {
              // Sort in MX priority (higher number is lower priority)
                Arrays.sort(records, new Comparator<Record>() {
                    @Override
                    public int compare(Record arg0, Record arg1) {
                        return ((MXRecord)arg0).getPriority()-((MXRecord)arg1).getPriority();
                    }
                });
                // Create records collection
                recordsColl = new Vector<URLName>(records.length);
                for (int i = 0; i < records.length; i++)
        { 
          MXRecord mx = (MXRecord) records[i];
          String targetString = mx.getTarget().toString();
          URLName uName = new URLName(
              SMTPScheme +
              targetString.substring(0, targetString.length() - 1)
          );
          recordsColl.add(uName);
        }
            }else
            {
              foundOriginalMX = false;
              recordsColl = new Vector<URLName>();
            }
            
            /*
             * If we found no MX record for the original hostname (the upper 
             * level domains does not matter), then we add the original domain 
             * name (identified with an A record) to the record collection, 
             * because the mail exchange server could be the main server too.
       * 
       * We append the A record to the first place of the record 
       * collection, because the standard says if no MX record found then 
       * we should to try send email to the server identified by the A 
       * record.
             */
      if( !foundOriginalMX )
      {
        Record[] recordsTypeA = new Lookup(hostName, Type.A).run();
        if (recordsTypeA != null && recordsTypeA.length > 0)
        {
          recordsColl.add(0, new URLName(SMTPScheme + hostName));
        }
      }

    } catch (TextParseException e) {
      log.warn("failed get MX record", e);
    }

    return recordsColl;

  }
  
}
