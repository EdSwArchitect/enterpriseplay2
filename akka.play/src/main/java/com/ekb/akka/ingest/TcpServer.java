package com.ekb.akka.ingest;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import com.ekb.akka.networking.SimplisticHandler;

import java.net.InetSocketAddress;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class TcpServer extends UntypedActor {
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    final ActorRef manager;

    /**
     *
     * @param manager
     */
    public TcpServer(ActorRef manager) {
        this.manager = manager;
    }

    /**
     *
     * @param manager
     * @return
     */
    public static Props props(ActorRef manager) {

        return Props.create(TcpServer.class, manager);
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void preStart() throws Exception {
        log.info("preStart***");

        final ActorRef tcp = Tcp.get(getContext().system()).manager();

        tcp.tell(TcpMessage.bind(getSelf(),
                new InetSocketAddress("localhost", 8555), 100), getSelf());

        log.info("Bound done");
    }

    /**
     *
     * @param msg
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {

        if (msg instanceof Tcp.Bound) {
            log.info("Got bound message");

            manager.tell(msg, getSelf());
        } // if (msg instanceof Tcp.Bound) {
        else if (msg instanceof Tcp.CommandFailed) {

            Tcp.CommandFailed failed = (Tcp.CommandFailed)msg;

            log.error("Tcp.Command failed: " + failed.cmd().failureMessage());

            getContext().stop(getSelf());

        } // else if (msg instanceof Tcp.CommandFailed) {
        else if (msg instanceof Tcp.Connected) {

            final Tcp.Connected conn = (Tcp.Connected) msg;

            log.info("Connection completed to remote address: " + conn.remoteAddress());

            manager.tell(conn, getSelf());

            final ActorRef handler = getContext().actorOf(
                    Props.create(NewlineHandler.class), "NewlineHandler");

            getSender().tell(TcpMessage.register(handler), getSelf());

        } // else if (msg instanceof Tcp.Connected) {
        else {
            log.error("Message is of type: " + msg.getClass().getName());
            unhandled(msg);
        }

    }
}
