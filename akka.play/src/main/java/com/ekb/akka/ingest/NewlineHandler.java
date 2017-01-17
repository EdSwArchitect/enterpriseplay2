package com.ekb.akka.ingest;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.util.ByteIterator;
import akka.util.ByteString;
import com.ekb.akka.networking.Buffering;

import java.io.ByteArrayOutputStream;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class NewlineHandler extends UntypedActor{
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** byte buffer */
//    private ByteArrayOutputStream baos;

    private ActorRef buffering;

    /**
     * Constructor
     */
    public NewlineHandler() {
//        baos = new ByteArrayOutputStream();
        buffering = context().actorOf(Props.create(Buffering.class), "Buffer");
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

//            log.info("data: " + data.utf8String());
//            log.info("Sender is: " + getSender().path());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ByteIterator bi = data.iterator();

            while (bi.hasNext()) {
                baos.write(bi.next());
            } // while (bi.hasNext()) {

            log.info("SimplisticHandler::sender() " + getSender().path());

            //getSender().tell(TcpMessage.write(data), getSelf());
            buffering.tell(baos, getSelf());

        } // if (msg instanceof Tcp.Received) {
        else if (msg instanceof Tcp.ConnectionClosed) {
            log.info("TCP connection closed");
            getContext().stop(getSelf());
        }
    }

}
