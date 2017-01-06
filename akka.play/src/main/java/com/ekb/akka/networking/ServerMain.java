package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.akka.es.EsStorage;

import java.util.concurrent.TimeUnit;

/**
 * Created by EdwinBrown on 1/5/2017.
 */
public class ServerMain {
    public static void main(String... args) {
        try {
            ActorSystem system = ActorSystem.create("EdNetworking");
            ActorRef serverMgr = system.actorOf(Props.create(ServerMgr.class), "EdServer");

            TimeUnit.MINUTES.sleep(3L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
