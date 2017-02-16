package com.ekb.cyber.networking.tcp;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;

import java.net.InetSocketAddress;

/**
 * Created by ebrown on 2/16/2017.
 */
public class SyslogTcp extends UntypedActor {
    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private ActorRef tcpManager;
    private ActorRef parser;
    private ActorRef handler;
    private String hostName;
    private int port;

    /**
     *
     * @param hostName
     * @param port
     */
    public SyslogTcp(String hostName, int port) {
        tcpManager = Tcp.get(getContext().system()).manager();
        this.hostName = hostName;
        this.port = port;
        handler = getContext().actorOf(
                Props.create(SyslogHandler.class), "SyslogHandler");
    }

    /**
     *
     * @param hostName
     * @param port
     * @param parser
     */
    public SyslogTcp(String hostName, int port, ActorRef parser) {
        tcpManager = Tcp.get(getContext().system()).manager();
        this.hostName = hostName;
        this.port = port;
        this.parser = parser;
        handler = getContext().actorOf(
                Props.create(SyslogHandler.class, parser), "SyslogHandler");
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
                new InetSocketAddress(hostName, port), 100), getSelf());

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
            log.info("Got bound message: " + msg);

            tcpManager.tell(msg, getSelf());

        } // if (msg instanceof Tcp.Bound) {
        else if (msg instanceof Tcp.CommandFailed) {

            Tcp.CommandFailed failed = (Tcp.CommandFailed)msg;

            log.error("Tcp.Command failed: " + failed.cmd().failureMessage());

            getContext().stop(getSelf());

        } // else if (msg instanceof Tcp.CommandFailed) {
        else if (msg instanceof Tcp.Connected) {

            final Tcp.Connected conn = (Tcp.Connected) msg;

            log.info("Connection completed to remote address: " + conn.remoteAddress());

            tcpManager.tell(conn, getSelf());

//            final ActorRef handler = getContext().actorOf(
//                    Props.create(SyslogHandler.class), "SyslogHandler");

            getSender().tell(TcpMessage.register(handler), getSelf());

        } // else if (msg instanceof Tcp.Connected) {
        else {
            log.error("Message is of type: " + msg.getClass().getName());
            unhandled(msg);
        }

    }
}
