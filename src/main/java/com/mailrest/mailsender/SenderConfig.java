/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SenderConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(SenderConfig.class);

	public static final String CASSANDRA_HOST = "cassandra.host";
	public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";

	public static final String BUCKET_ID = "mailsender.bucket.id";
	public static final String PULL_INTERVAL_SECONDS = "mailsender.pull.interval.seconds";
	public static final String REPEAT_INTERVAL_SECONDS = "mailsender.repeat.interval.seconds";
	public static final String REPEAT_MAX_TRIES = "mailsender.repeat.max.tries";

	public static final String SENDER_CONF = "../conf/mailsender.conf";

	private static final String SYS_USER = System.getProperty("user");
	private static final String SYS_HOST = System.getProperty("host");

	private final Properties props = new Properties();

	private final String host;
	private final String jvmId;
	private final String externalIp;
	
	public SenderConfig() {

		File senderConf = new File(SENDER_CONF);
		if (!senderConf.exists()) {
			String msg = "config file not found " + SENDER_CONF;
			logger.error(msg);
			throw new ConfigurationException(msg);
		}

		try {
			props.load(new FileInputStream(senderConf));
		} catch (IOException e) {
			throw new ConfigurationException("io erorr on "
					+ senderConf.getAbsolutePath(), e);
		}

		host = detectHost();
		jvmId = detectJmvId(host);
		externalIp = detectExternalIp();
	}

	public int getPullIntervalSec() {
		String val = props.getProperty(PULL_INTERVAL_SECONDS);
		if (val == null) {
			return Defaults.DEF_PULL_INTERVAL_SECONDS;
		}
		return Integer.parseInt(val);
	}
	
	public int getRepeatIntervalSec() {
		String val = props.getProperty(REPEAT_INTERVAL_SECONDS);
		if (val == null) {
			return Defaults.DEF_REPEAT_INTERVAL_SECONDS;
		}
		return Integer.parseInt(val);
	}
	
	public int getRepeatMaxTries() {
		String val = props.getProperty(REPEAT_MAX_TRIES);
		if (val == null) {
			return Defaults.DEF_REPEAT_MAX_TRIES;
		}
		return Integer.parseInt(val);
	}

	public String getCassandraHost() {
		return props.getProperty(CASSANDRA_HOST, Defaults.DEF_CASSANDRA_HOST);
	}

	public String getCassandraKeyspace() {
		return props.getProperty(CASSANDRA_KEYSPACE, Defaults.DEF_CASSANDRA_KEYSPACE);
	}
	
	public int getBucketId() {
		String val = props.getProperty(BUCKET_ID);
		if (val == null) {
			throw new IllegalStateException(BUCKET_ID + " not found in mailsender.conf");
		}
		return Integer.parseInt(val);
	}

	public String getUser() {
		return SYS_USER;
	}

	public String getHost() {
		return host;
	}
	
	public String getJvmId() {
		return jvmId;
	}
	
	public String getExternalIp() {
		return externalIp;
	}
	
	private String detectExternalIp() {
		try {
			URL publicIp = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
			                publicIp.openStream()));

			return in.readLine();
		} catch (Exception e) {
			logger.error("fail get ip from aws", e);
			return "127.0.0.1";
		}
	}
	
	private  String detectHost() {
		String host = SYS_HOST;
		try {
			host = SYS_HOST != null ? SYS_HOST : InetAddress.getLocalHost().getCanonicalHostName();
			if (host.indexOf(".") == -1) {
				// we need to have full host name
				host = host + ".com";
			}
		} catch (Exception e) {
			logger.error("fail get localhost", e);
		}
		return host;
	}
	
	private String detectJmvId(String defaultJvmId) {
		try {
			java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory
					.getRuntimeMXBean();			
			return runtime.getName();
		} catch (Exception e) {
			logger.error( "fail to get jvm id", e);
			return defaultJvmId;
		}
	}

}
