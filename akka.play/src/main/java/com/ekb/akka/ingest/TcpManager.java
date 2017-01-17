package com.ekb.akka.ingest;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import com.ekb.akka.networking.Server;

/**
 * AKKA actor class to access network stuff. I'm just learning stuff.
 */
public class TcpManager extends UntypedActor {
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** server */
    private ActorRef server;

    public TcpManager() {
        server = context().actorOf(Props.create(TcpServer.class, Tcp.get(getContext().system()).manager()), "TcpServer");
    }

    /**
     * Message received for the actor. If one of the commands, performs the ElasticSearch operation. For bulk requests,
     * a timer is set for 30 seconds. At the expiration of 30 seconds, if there is any data to be inserted, the left
     * over data is inserted.
     * @param message The message received.
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        log.info("message class : " + message.getClass().getName());

        if (message instanceof Tcp.Bound) {
            log.info("Server says it got the bound message");
        }
    }

    @Override
    public void preStart() throws Exception {
        log.info("preStart");
    }

    /**
     * At the stopping of the application, close the ElasticSearch connection
     * @throws Exception
     */
    @Override
    public void postStop() throws Exception {
        log.info("postStop");
    }
}
