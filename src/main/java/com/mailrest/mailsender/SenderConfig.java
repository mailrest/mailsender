package com.mailrest.mailsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SenderConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(SenderConfig.class);

	public static final String CASSANDRA_HOST = "cassandra.host";
	public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";

	public static final String BUCKET_ID = "mailsender.bucket.id";
	public static final String PULL_INTERVAL_SEC = "mailsender.pull.interval.seconds";

	public static final String SENDER_CONF = "../conf/mailsender.conf";

	private static final String SYS_USER = System.getProperty("user");
	private static final String SYS_HOST = System.getProperty("host");

	private final Properties props = new Properties();

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

	}

	public int getPullIntervalSec() {
		String val = props.getProperty(PULL_INTERVAL_SEC);
		if (val == null) {
			return Defaults.DEF_PULL_INTERVAL_SECONDS;
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
		return SYS_HOST;
	}

}
