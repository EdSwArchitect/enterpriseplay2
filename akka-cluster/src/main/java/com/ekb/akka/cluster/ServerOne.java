package com.ekb.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.concurrent.TimeUnit;

/**
 * Created by EdwinBrown on 2/24/2017.
 */
public class ServerOne {
    public static void main(String[] args) {
        try {

            System.setProperty("akka.remote.netty.port", "2251");

            ActorSystem system = ActorSystem.create("ServerCluster");
            ActorRef serverOne = system.actorOf(Props.create(Member.class, "MemberOne"), "FirstServer");

            system.log().info("Hi ed");

            TimeUnit.MINUTES.sleep(5L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
