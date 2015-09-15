/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.mail.URLName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNSResolverImpl implements DNSResolver {

	private static final Logger logger = LoggerFactory.getLogger(DNSResolverImpl.class);

	public List<URLName> getMXRecordsForHost(String hostName) {

		List<URLName> result = new ArrayList<URLName>();
		try {

			Record[] records = new Lookup(hostName, Type.MX).run();

			/*
			 * Search in sub-domains
			 */
			if (records == null || records.length == 0) {

				String upperLevelHostName = hostName;
				while (records == null
						&& upperLevelHostName.indexOf(".") != upperLevelHostName
								.lastIndexOf(".")
						&& upperLevelHostName.lastIndexOf(".") != -1) {
					upperLevelHostName = upperLevelHostName
							.substring(upperLevelHostName.indexOf(".") + 1);
					records = new Lookup(upperLevelHostName, Type.MX).run();
				}
			}

			if (records != null && records.length > 0) {
				
				// Sort in MX priority 
				Arrays.sort(records, MXRecordComparator.INSTANCE);
				
				// Create records collection
				result = new ArrayList<URLName>(records.length);
				for (int i = 0; i < records.length; i++) {
					MXRecord mx = (MXRecord) records[i];
					String targetString = mx.getTarget().toString();
					URLName uName = new URLName(SMTPScheme
							+ targetString.substring(0,
									targetString.length() - 1));
					result.add(uName);
				}
			} 

			/*
             * if no MX record found then we should to try send email to the server identified by the A record.
			 */
			if (result.isEmpty()) {
				Record[] recordsTypeA = new Lookup(hostName, Type.A).run();
				if (recordsTypeA != null && recordsTypeA.length > 0) {
					result.add(0, new URLName(SMTPScheme + hostName));
				}
			}

		} catch (TextParseException e) {
			logger.warn("failed get MX record for " + hostName, e);
		}

		return result;

	}
	
	public enum MXRecordComparator implements Comparator<Record> {
		
		INSTANCE;
		
		@Override
		public int compare(Record thisRec, Record otherRec) {
			return ((MXRecord) thisRec).getPriority()
					- ((MXRecord) otherRec).getPriority();
		}
		
	}

}
