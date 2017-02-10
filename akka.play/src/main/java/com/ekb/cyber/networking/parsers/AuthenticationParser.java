package com.ekb.cyber.networking.parsers;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/10/2017.
 */
public class AuthenticationParser extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /**
     *
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        log.info("Message received: " + message);
        unhandled(message);
    }
}
