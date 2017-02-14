package com.ekb.cyber.networking.metrics;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/14/2017.
 */
public class MetricsListener extends UntypedActor {

    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    Cluster cluster;

//    private final Cluster cluster = Cluster.get(getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Throwable {
        log.info("Message received: " + message);

        unhandled(message);
    }
}
