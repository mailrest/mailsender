#!/usr/bin/env bash

export DAEMON_NAME=mailsender
export PIDFILE=./mailsender.pid
export MAIN=com.mailrest.mailsender.SenderDaemon
export DEBUG=off

BIN=`dirname "$0"`

. $BIN/daemon $@

