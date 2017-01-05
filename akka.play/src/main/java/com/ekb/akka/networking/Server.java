package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;

import java.net.InetSocketAddress;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class Server extends UntypedActor {
    final ActorRef manager;
    public Server(ActorRef manager) {
        this.manager = manager;
    }
    public static Props props(ActorRef manager) {
        return Props.create(Server.class, manager);
    }
    @Override
    public void preStart() throws Exception {
        final ActorRef tcp = Tcp.get(getContext().system()).manager();
        tcp.tell(TcpMessage.bind(getSelf(),
                new InetSocketAddress("localhost", 0), 100), getSelf());
    }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Tcp.Bound) {
            manager.tell(msg, getSelf());
        } // if (msg instanceof Tcp.Bound) {
        else if (msg instanceof Tcp.CommandFailed) {
            getContext().stop(getSelf());
        } // else if (msg instanceof Tcp.CommandFailed) {
        else if (msg instanceof Tcp.Connected) {
            final Tcp.Connected conn = (Tcp.Connected) msg;
            manager.tell(conn, getSelf());
            final ActorRef handler = getContext().actorOf(
                    Props.create(SimplisticHandler.class));
            getSender().tell(TcpMessage.register(handler), getSelf());
        }
        else {
            unhandled(msg);
        }

    }
}
