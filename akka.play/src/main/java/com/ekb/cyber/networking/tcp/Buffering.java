package com.ekb.cyber.networking.tcp;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/17/2017.
 */
public class Buffering extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** flag for */
    private boolean synching = true;
    /** next actor */
    private ActorRef nextActor;
    /** buffer */
    private StringBuilder buffer;

    public Buffering() {
        buffer = new StringBuilder();
        log.debug("Buffering created");
    }

    /**
     *
     * @param nextActor
     */
    public Buffering(ActorRef nextActor) {
        buffer = new StringBuilder();
        this.nextActor = nextActor;

        log.debug("Buffering created with nextActor: " + nextActor);
    }


    /**
     *
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        log.debug("message: " + message.toString());

        if (message instanceof String) {
            String data = (String)message;

            // skip to new lines
            if (synching) {
                int recSepIndex = data.indexOf("\n\n");

                if (recSepIndex >= 0) {
                    data = ((String) message).substring(recSepIndex+2);
                    synching = false;

                    log.debug("***>>> Synchronized new lines");
                } // if (recSepIndex >= 0) {
                else {
                    log.debug("Unable to sync: '" + data + "'");
                }

            } // if (synching) {

            // only do the rest if you are not dropping data
            if (!synching) {
                buffer.append(data);

                data = buffer.toString();

                buffer.setLength(0);

                int endIndex = data.indexOf("\n\n");
                int startIndex = 0;

                while (endIndex >= 0) {
                    String line = data.substring(startIndex, endIndex);

                    if (nextActor != null) {
                        nextActor.tell(line, getSelf());
                    }
                    else {
                        log.info("Line of data: '" + line + "'");
                    }

                    startIndex = endIndex+2;
                    endIndex = data.indexOf("\n\n", startIndex);
                } // while (endIndex >= 0) {

                if (endIndex < 0) {
                    buffer.append(data.substring(startIndex));
                }
            }
        }
        else {
            unhandled(message);
        }
    }
}
