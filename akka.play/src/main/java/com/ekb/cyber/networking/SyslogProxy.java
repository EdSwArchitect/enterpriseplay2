package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import com.ekb.SyslogWebProxyApplication;

/**
 * Created by ebrown on 2/20/2017.
 */
public class SyslogProxy {
    String hostName;
    int port;
    String espUri;
    ActorSystem system;
    ActorRef webProxy;
    LoggingAdapter log;

    public SyslogProxy(String hostName, int port, String espUri) {
        this.hostName = hostName;
        this.espUri = espUri;
        this.port = port;

        system = ActorSystem.create("TcpNetwork");
        log = system.log();
        webProxy = system.actorOf(Props.create(SyslogWebProxyApplication.class, hostName, port, espUri), "WebProxy");

        log.info("Hostname: " + hostName);
        log.info("Port: " + port);
        log.info("espUri: " + espUri);
    }

    public void start() {
        log.info("Starting web proxy processing");
        webProxy.tell(SyslogWebProxyApplication.COMMAND.START, null);
    }

    public void stop() {
        log.info("Stopping web proxy processing");
        webProxy.tell(SyslogWebProxyApplication.COMMAND.STOP, null);
    }
}
