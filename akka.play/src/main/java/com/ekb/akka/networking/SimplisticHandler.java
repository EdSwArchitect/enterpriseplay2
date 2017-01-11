package com.ekb.akka.networking;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteIterator;
import akka.util.ByteString;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class SimplisticHandler extends UntypedActor{
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** byte buffer */
    private ByteArrayOutputStream baos;

    /**
     * Constructor
     */
    public SimplisticHandler() {
        baos = new ByteArrayOutputStream();
    }

    /**
     * Get the data and buffer it
     * @param msg
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.Received) {
            Tcp.Received tcpReceived = (Tcp.Received)msg;

            final ByteString data = tcpReceived.data();

            log.info("data: " + data.utf8String());
            log.info("Sender is: " + getSender().path());

            ByteIterator bi = data.iterator();

            while (bi.hasNext()) {
                baos.write(bi.next());
            } // while (bi.hasNext()) {


            getSender().tell(TcpMessage.write(data), getSelf());

        } // if (msg instanceof Tcp.Received) {
        else if (msg instanceof Tcp.ConnectionClosed) {
            log.info("TCP connection closed");
            getContext().stop(getSelf());
        }
    }

}
