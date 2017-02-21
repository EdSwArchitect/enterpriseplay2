#!/bin/bash
source /opt/SASHome/SASEventStreamProcessingEngine/4.1.0/conf/startup
DFESP_HOME=/opt/SASHome/SASEventStreamProcessingEngine/4.1.0
PATH=$DFESP_HOME/bin:$PATH
$DFESP_HOME/bin/dfesp_fs_adapter -k sub -h dfESP://localhost:9095/AkkaProject/Authentication_Query/Authentication?snapshot=false -t csv -f /tmp/auth.csv -c 120&
$DFESP_HOME/bin/dfesp_fs_adapter -k sub -h dfESP://localhost:9095/AkkaProject/WebProxyIngest_Query/WebProxy?snapshot=false -t csv -f /tmp/proxy.csv -c 120&
