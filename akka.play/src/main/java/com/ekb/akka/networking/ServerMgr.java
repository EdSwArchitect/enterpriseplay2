package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;

/**
 * AKKA actor class to access Elastic Search. I'm just learning stuff.
 * Created by EdwinBrown on 12/22/2016.
 */
public class ServerMgr extends UntypedActor {
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** server */
    private ActorRef server;

    public ServerMgr() {
//        server = context().actorOf(Props.create(Server.class, this), "The server");
        server = context().actorOf(Props.create(Server.class, Tcp.get(getContext().system()).manager()), "TheServer");
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
