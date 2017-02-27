package com.ekb.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.concurrent.TimeUnit;

/**
 * Created by EdwinBrown on 2/26/2017.
 */
public class SameServer {
    public static void main(String[] args) {
        try {
            ActorSystem system = ActorSystem.create("ServerCluster");

            System.setProperty("akka.remote.netty.port", "2251");

            ActorRef serverOne = system.actorOf(Props.create(Member.class, "MemberOne")); //, "FirstServer");

            system.log().info("Hi ed");

            System.setProperty("akka.remote.netty.port", "2252");

            ActorRef serverTwo = system.actorOf(Props.create(Member.class, "MemberOne")); //, "SecondServer");

            TimeUnit.MINUTES.sleep(5L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
