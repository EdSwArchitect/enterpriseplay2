package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import com.ekb.SyslogAuthApplication;

/**
 * Created by ebrown on 2/20/2017.
 */
public class SyslogAuth {
    private String hostName;
    private int port;
    private String espUri;
    ActorRef auth;
    ActorSystem system;
    LoggingAdapter log;

    /**
     * Constructor
     * @param hostName
     * @param port
     * @param espUri
     */
    public SyslogAuth(String hostName, int port, String espUri) {
        this.hostName = hostName;
        this.port = port;
        this.espUri = espUri;

        try {

            system = ActorSystem.create("UdpNetwork");

            log = system.log();

            auth = system.actorOf(Props.create(SyslogAuthApplication.class, hostName, port, espUri), "Auth");

            log.info("Starting the authentication application...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        log.info("Starting auth...");

        auth.tell(SyslogAuthApplication.COMMAND.START, null);
    }

    public void stop() {

        log.info("Stopping auth");

        auth.tell(SyslogAuthApplication.COMMAND.STOP, null);
    }
}
