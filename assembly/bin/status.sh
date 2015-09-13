#!/usr/bin/env bash

BIN=`dirname "$0"`
pushd $BIN/../log 1> /dev/null

. ../bin/config

MAIN=com.mailrest.mailsender.StatusApp

"$JAVA" -server -classpath ${CLASSPATH} ${JAVA_OPTS} ${ARGS} -Duser="$USER" -Dhost=`hostname` "$MAIN" $@

popd 1> /dev/null
