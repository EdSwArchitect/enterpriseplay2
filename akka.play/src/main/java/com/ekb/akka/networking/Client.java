package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.japi.Procedure;
import akka.util.ByteString;

import java.net.InetSocketAddress;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class Client extends UntypedActor {
    final InetSocketAddress remote;
    final ActorRef listener;

    public static Props props(InetSocketAddress remote, ActorRef listener) {
        return Props.create(Client.class, remote, listener);
    }

    public Client(InetSocketAddress remote, ActorRef listener) {
        this.remote = remote;
        this.listener = listener;
        final ActorRef tcp = Tcp.get(getContext().system()).manager();
        tcp.tell(TcpMessage.connect(remote), getSelf());
    }


    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.CommandFailed) {
            listener.tell("failed", getSelf());
            getContext().stop(getSelf());
        } // if (msg instanceof Tcp.CommandFailed) {
        else if (msg instanceof Tcp.Connected) {
            listener.tell(msg, getSelf());
            getSender().tell(TcpMessage.register(getSelf()), getSelf());
            getContext().become(connected(getSender()));
        }
    }

    private Procedure<Object> connected(final ActorRef connection) {
        return new Procedure<Object>() {
            @Override
            public void apply(Object msg) throws Exception {
                if (msg instanceof ByteString) {
                    connection.tell(TcpMessage.write((ByteString) msg), getSelf());
                } else if (msg instanceof Tcp.CommandFailed) {
// OS kernel socket buffer was full
                } else if (msg instanceof Tcp.Received) {
                    listener.tell(((Tcp.Received) msg).data(), getSelf());
                } else if (msg.equals("close")) {
                    connection.tell(TcpMessage.close(), getSelf());
                } else if (msg instanceof Tcp.ConnectionClosed) {
                    getContext().stop(getSelf());
                }
            }
        };
    }
}
