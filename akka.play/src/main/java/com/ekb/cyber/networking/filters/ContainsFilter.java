package com.ekb.cyber.networking.filters;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/10/2017.
 */
public class ContainsFilter extends UntypedActor{

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** filtering for */
    private String lookingFor;
    /** the next actor in the pipe line */
    private ActorRef nextActor;

    /**
     *
     * @param lookingFor
     * @param nextActor
     */
    public ContainsFilter(String lookingFor, ActorRef nextActor) {
        this.lookingFor = lookingFor;
        this.nextActor = nextActor;
    }

    /**
     *
     * @param lookingFor
     * @param nextActor
     * @return
     */
    public static Props props(String lookingFor, ActorRef nextActor) {

        return Props.create(ContainsFilter.class, lookingFor, nextActor);
    }

    /**
     *
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
//        log.info("Message received: " + message);

        if (message instanceof String) {
            String text = (String)message;

            if (text.contains(lookingFor)) {
                nextActor.tell(text, getSelf());
            } // if (text.contains(lookingFor)) {
//            else {
//                log.info("Dropping: " + text);
//            }

        } // if (message instanceof String) {
        else {
            unhandled(message);
        }
    }
}
