package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.concurrent.TimeUnit;

/**
 * Created by EdwinBrown on 1/5/2017.
 */
public class ServerMain {
    public static void main(String... args) {
        try {
            ActorSystem system = ActorSystem.create("EdNetworking");
            ActorRef output = system.actorOf(Props.create(OutputActor.class), "Output");
            ActorRef serverMgr = system.actorOf(Props.create(ServerMgr.class), "EdServer");

            TimeUnit.MINUTES.sleep(3L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
