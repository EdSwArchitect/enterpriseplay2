package com.ekb.akka.networking;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 1/10/2017.
 */
public class OutputActor extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public void onReceive(Object message) throws Throwable {
        log.error(">>>>> " + message.toString());
    }
}
