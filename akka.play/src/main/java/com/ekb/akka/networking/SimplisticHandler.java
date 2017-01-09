package com.ekb.akka.networking;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class SimplisticHandler extends UntypedActor{
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.Received) {
            Tcp.Received tcpReceived = (Tcp.Received)msg;

            final ByteString data = tcpReceived.data();

            log.info("data: " + data.utf8String());
            log.info("Sender is: " + getSender().path());

            getSender().tell(TcpMessage.write(data), getSelf());

        } // if (msg instanceof Tcp.Received) {
        else if (msg instanceof Tcp.ConnectionClosed) {
            log.info("TCP connection closed");
            getContext().stop(getSelf());
        }
    }
}
