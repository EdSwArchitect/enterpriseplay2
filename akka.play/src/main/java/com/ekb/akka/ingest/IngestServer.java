package com.ekb.akka.ingest;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 1/17/2017.
 */
public class IngestServer {
    public static void main(String ... args) {
        try {
            ActorSystem system = ActorSystem.create("IngestServer");

            LoggingAdapter log = system.log();

            log.info("Hi Ed");

            ActorRef tcpManager = system.actorOf(Props.create(TcpManager.class), "TcpManager");

            log.info("Got tcp manager");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
