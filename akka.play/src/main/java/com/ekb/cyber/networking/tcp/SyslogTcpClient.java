package com.ekb.cyber.networking.tcp;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

import java.net.InetSocketAddress;

/**
 * Created by ebrown on 2/16/2017.
 */
public class SyslogTcpClient extends UntypedActor {
    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /** The host name */
    private String hostName;
    /** the port */
    private int port;
    /** the TCP manager */
    private ActorRef tcpManager;

    /**
     *
     * @param hostName
     * @param port
     * @return
     */
    public static Props props(String hostName,int port) {
        return Props.create(SyslogTcpClient.class, hostName, port);
    }

    /**
     *
     * @param hostName
     * @param port
     */
    public SyslogTcpClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;

        tcpManager = Tcp.get(getContext().system()).manager();

        tcpManager.tell(TcpMessage.connect(new InetSocketAddress(hostName, port)), getSelf());

        log.info("Client created");
    }


    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.CommandFailed) {
            log.error("Tcp.CommandFailed: " + msg);

//            listener.tell("failed", getSelf());
            getContext().stop(getSelf());
        } // if (msg instanceof Tcp.CommandFailed) {
        else if (msg instanceof Tcp.Connected) {
//            listener.tell(msg, getSelf());

            log.info("Tcp.Connected: " + msg);

            getSender().tell(TcpMessage.register(getSelf()), getSelf());
            getContext().become(connected(getSender()));
        }
    }

    private Procedure<Object> connected(final ActorRef connection) {
        return new Procedure<Object>() {
            @Override
            public void apply(Object msg) throws Exception {
                if (msg instanceof ByteString) {

                    log.info("ByteString: " + msg);

                    // connection.tell(TcpMessage.write((ByteString) msg), getSelf());
                } else if (msg instanceof Tcp.CommandFailed) {

                    // OS kernel socket buffer was full
                    log.error("Tcp.CommandFailed: " + msg);
                } else if (msg instanceof Tcp.Received) {
//                    listener.tell(((Tcp.Received) msg).data(), getSelf());

                    Tcp.Received data = (Tcp.Received)msg;

                    byte[] bytes = data.data().toArray();

                    log.info("data received: " + new String(bytes, "UTF-8"));

                } else if (msg.equals("close")) {
                    log.info("Msg close");
                    connection.tell(TcpMessage.close(), getSelf());
                } else if (msg instanceof Tcp.ConnectionClosed) {

                    log.info("ConnectionClosed: " + msg);

                    getContext().stop(getSelf());
                }
            }
        };
    }

}
