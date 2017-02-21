#!/bin/bash
source /opt/SASHome/SASEventStreamProcessingEngine/4.1.0/conf/startup
DFESP_HOME=/opt/SASHome/SASEventStreamProcessingEngine/4.1.0
PATH=$DFESP_HOME/bin:$PATH
dfesp_xml_server -http-admin 9080 -http-pubsub 9090 -pubsub 9095 -loglevel esp=trace -model file:///root/akka-play/Akka-Project.xml &
