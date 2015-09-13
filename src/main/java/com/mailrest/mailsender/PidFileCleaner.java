package com.mailrest.mailsender;

import java.io.File;

public final class PidFileCleaner {

	public static final String PID_FILE = System.getProperty("pidFile");

	private PidFileCleaner() {
	}

	public static void deletePidFileOnExit() {
		if (PID_FILE != null) {
			File pidFile = new File(PID_FILE);
			if (pidFile.exists()) {
				pidFile.deleteOnExit();
			}
		}
	}

	public static void deletePidFile() {
		if (PID_FILE != null) {
			File pidFile = new File(PID_FILE);
			if (pidFile.exists()) {
				pidFile.delete();
			}
		}
	}
}
