package com.ekb.akka.es;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.akka.parse.ParseLog;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Class to kick off the Actor system
 * Created by EdwinBrown on 12/22/2016.
 */
public class ESDelIndexMain {
    public static void main(String... args) {
        System.out.println("Startup");
        ActorSystem system = ActorSystem.create("EsCleanup");

        ActorRef storage = system.actorOf(Props.create(EsStorage.class), "ES_Storage");

        // ES_COMMAND command, String index, String document, String json
        EsStorage.EsCmd cmd = new EsStorage.EsCmd(EsStorage.ES_COMMAND.DELETE, "router", "log", "");

        storage.tell(cmd, ActorRef.noSender());

        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("About to shut things down");

//        system.stop(storage);
        system.terminate();


    }
}
