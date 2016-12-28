package com.ekb.akka.starting;

import akka.actor.UntypedActor;

/**
 * Created by EdwinBrown on 12/7/2016.
 */
public class Greeter extends UntypedActor {
    public static enum Msg { GREET, DONE }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg == Msg.GREET) {
            System.out.println("Hello world!");
            this.getSender().tell(Msg.DONE, this.getSelf());
        } // if (msg == Msg.GREET) {
        else {
            this.unhandled(msg);
        }

    }
}
