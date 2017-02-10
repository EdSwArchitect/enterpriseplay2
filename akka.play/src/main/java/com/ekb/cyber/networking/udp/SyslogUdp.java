package com.ekb.cyber.networking.udp;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;

import java.net.InetSocketAddress;

/**
 * Created by ebrown on 2/9/2017.
 * Page 455 in Akka manual
 */
public class SyslogUdp extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** next in line */
    private ActorRef nextActor;

    /**
     * Constructor
     * @param port The port it attempts to bind to to listen
     */
    public SyslogUdp(int port, ActorRef nextActor) {
        this.nextActor = nextActor;

        log.info("Attempting to listen to port: " + port + " with actor: " + nextActor);

        final ActorRef mgr = Udp.get(getContext().system()).getManager();

        mgr.tell(UdpMessage.bind(getSelf(), new InetSocketAddress(port)), getSelf());
    }

    /**
     *
     * @param port
     * @return
     */
    public static Props props(int port, ActorRef nextActor) {

        return Props.create(UdpServer.class, port, nextActor);
    }


    /**
     * Get the messages.
     * If bound, change context to
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
//        log.info("Message received: " + message);

        if (message instanceof Udp.Bound) {
            final Udp.Bound bound = (Udp.Bound)message;

            getContext().become(ready(getSender()));
        }
        else {
            unhandled(message);
        }
    }

    /**
     *
     * @param socket
     * @return
     */
    private Procedure<Object> ready(final ActorRef socket) {
        return new Procedure<Object>() {

            @Override
            public void apply(Object msg) throws Exception {
                if (msg instanceof Udp.Received) {
//                    log.info("Received data");

                    Udp.Received received = (Udp.Received)msg;

                    String utf8 = received.data().utf8String();
//                    log.info(utf8);

                    // process data here
                    if (nextActor != null) {
                        nextActor.tell(utf8, getSelf());
                    }

                } // if (msg instanceof Udp.Received) {
                else if (msg.equals(UdpMessage.unbind())) {
                    // socket closed
                    socket.tell(msg, getSelf());
                } // else if (msg instanceof Udp.Unbound) {
                else if (msg instanceof Udp.Unbound) {
                    // shut it down
                    getContext().stop(getSelf());
                } // else if (msg instanceof Udp.Unbound) {
                else {
                    unhandled(msg);
                }
            }
        };
    }

}
