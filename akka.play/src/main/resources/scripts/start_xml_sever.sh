#!/bin/bash
/opt/SASHome/SASEventStreamProcessingEngine/4.1.0/conf/startup
dfesp_xml_server -http-admin 9080 -http-pubsub 9090 -pubsub 9095 -loglevel esp=trace -model file:///root/akka-play/Akka-Project.xml &
