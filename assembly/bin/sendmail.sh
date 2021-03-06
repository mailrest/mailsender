#!/usr/bin/env bash

BIN=`dirname "$0"`
pushd $BIN/../log 1> /dev/null

. ../bin/config

MAIN=com.mailrest.mailsender.SendMailApp

"$JAVA" -server -classpath ${CLASSPATH} ${JAVA_OPTS} ${ARGS} -Duser="$USER" -Dhost="$MAILREST_HOST" "$MAIN" $@

popd 1> /dev/null
