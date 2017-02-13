package com.ekb.cyber.networking.parsers;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.regex.Pattern;

/**
 * Created by ebrown on 2/10/2017.
 */
public class WebProxyParser extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private final Pattern pattern = Pattern.compile("<\\d*>(\\w{3}) (\\d{2}:\\d{2}:\\d{2}) (\\w\\d\\.)+ (.*)");

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
