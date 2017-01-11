package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Tcp;

import java.util.concurrent.TimeUnit;

/**
 * Class to kick off the Actor system
 * Created by EdwinBrown on 12/22/2016.
 */
public class EsNetworkMain {
    public static void main(String... args) {
        System.out.println("Startup");
        ActorSystem system = ActorSystem.create("EsPlay");


        ActorRef tcpManager = Tcp.get(system).manager();

        System.out.println("Got TCP Manager");

        try {
            TimeUnit.MINUTES.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("About to shut things down");

        system.terminate();
    }
}
