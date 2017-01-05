package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

import java.net.InetSocketAddress;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class SimpleSender extends UntypedActor {

    final InetSocketAddress remote;

    public SimpleSender(InetSocketAddress remote) {
        this.remote = remote;

        // request creation of a SimpleSender
        final ActorRef mgr = Udp.get(getContext().system()).getManager();

        mgr.tell(UdpMessage.simpleSender(), getSelf());
    }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Udp.SimpleSenderReady) {
            getContext().become(ready(getSender()));
        } // if (msg instanceof Udp.SimpleSenderReady) {
        else {
            unhandled(msg);
        }
    }

    private Procedure<Object> ready(final ActorRef send) {
        return new Procedure<Object>() {
            @Override
            public void apply(Object msg) throws Exception {
                if (msg instanceof String) {
                    final String str = (String) msg;
                    send.tell(UdpMessage.send(ByteString.fromString(str), remote), getSelf());
                } // if (msg instanceof String) {
                else {
                    unhandled(msg);
                }
            }
        };
    }
}
