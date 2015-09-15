/*
 *      Copyright (C) 2015 Noorq, Inc.
 *      All rights reserved.
 */
package com.mailrest.mailsender;

public final class Defaults {

	private Defaults() {
		
	}
	
	public static final String DEF_SENDER_NAME = "mailrest.com";
	
	public static final String DEF_CASSANDRA_HOST = "localhost";
	
	public static final String DEF_CASSANDRA_KEYSPACE = "mailrest";
	
	public static final int DEF_PULL_INTERVAL_SECONDS = 100;
	
	public static final int DEF_REPEAT_INTERVAL_SECONDS = 86400;
	public static final int DEF_REPEAT_MAX_TRIES = 5;
	
	
}
