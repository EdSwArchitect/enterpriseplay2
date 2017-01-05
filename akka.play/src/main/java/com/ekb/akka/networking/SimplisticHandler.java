package com.ekb.akka.networking;

import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class SimplisticHandler extends UntypedActor{
    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.Received) {
            final ByteString data = ((Tcp.Received) msg).data();
            System.out.println(data);
            getSender().tell(TcpMessage.write(data), getSelf());
        } else if (msg instanceof Tcp.ConnectionClosed) {
            getContext().stop(getSelf());
        }
    }
}
