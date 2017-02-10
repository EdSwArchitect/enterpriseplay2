package com.ekb.cyber.networking.debugging;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/10/2017.
 */
public class Debugger extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /**
     * @return
     */
    public static Props props() {

        return Props.create(Debugger.class);
    }


    /**
     *
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        log.info("Received: " + message);
    }
}
