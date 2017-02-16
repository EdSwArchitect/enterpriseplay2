#!/bin/bash

# (c) 2015, SAS Institute Inc.

CD=`dirname $0`
cd $CD > /dev/null

addToClasspath() {
        local SOURCE=$1
        if [ ! -e $SOURCE ]; then
        	echo "$SOURCE is not installed. Exiting."
        	exit 1
        fi
        for f in $(find $SOURCE -name "*.jar" -print)
        do
                if [ ! -z "$CLASSPATH" ]; then
                        CLASSPATH="$CLASSPATH:$f"
                else
                        CLASSPATH="$f"
                fi
        done
}

CLASSPATH=""
addToClasspath ./libs

#echo $CLASSPATH

java -cp "$CLASSPATH" com.ekb.cyber.networking.SyslogAuthMain $*
