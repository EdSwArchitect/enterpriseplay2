package com.ekb.cyber.networking.tcp;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.util.ByteString;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class SyslogHandler extends UntypedActor{
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /** the next stage */
    private ActorRef nextActor;

    /**
     * Constructor
     */
    public SyslogHandler() {
    }

    /**
     *
     * @param nextActor
     */
    public SyslogHandler(ActorRef nextActor) {
        this.nextActor = nextActor;
    }

    /**
     * Get the data and buffer it and send the right things....
     * Doesn't sync up the end of record right now. :-(
     * @param msg
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.Received) {
            Tcp.Received tcpReceived = (Tcp.Received)msg;

            final ByteString data = tcpReceived.data();

            byte[] bytes = data.toArray();

            if (nextActor != null) {

                nextActor.tell(new String(bytes, "UTF-8"), getSelf());
            } // if (nextActor != null) {
            else {
                log.info("Bytes received as string: " + new String(bytes, "UTF-8"));
            }

//            log.info("data: " + data.utf8String());
//            log.info("Sender is: " + getSender().path());

//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            ByteIterator bi = data.iterator();
//
//            while (bi.hasNext()) {
//                baos.write(bi.next());
//            } // while (bi.hasNext()) {
//
//            log.info("SimplisticHandler::sender() " + getSender().path());
//
//            //getSender().tell(TcpMessage.write(data), getSelf());
//            buffering.tell(baos, getSelf());

        } // if (msg instanceof Tcp.Received) {
        else if (msg instanceof Tcp.ConnectionClosed) {
            log.info("TCP connection closed");
            getContext().stop(getSelf());
        }
    }

}
