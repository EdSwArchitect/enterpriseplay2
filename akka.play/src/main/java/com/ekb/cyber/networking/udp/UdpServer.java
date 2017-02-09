package com.ekb.cyber.networking.udp;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/9/2017.
 */
public class UdpServer extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /**
     *
     * @param manager
     * @return
     */
    public static Props props(ActorRef manager) {

        return Props.create(UdpServer.class, manager);
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        log.info("Message received: " + message);
        unhandled(message);
    }
}
