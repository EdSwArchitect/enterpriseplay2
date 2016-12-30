package com.ekb.akka.es;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.akka.parse.ParseLog;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Class to kick off the Actor system
 * Created by EdwinBrown on 12/22/2016.
 */
public class EsQueryMain {
    public static void main(String... args) {
        System.out.println("Startup");
        ActorSystem system = ActorSystem.create("EsPlay");

        ActorRef storage = system.actorOf(Props.create(EsStorage.class), "ES_Storage");

        EsStorage.EsCmd cmd = new EsStorage.EsCmd(EsStorage.ES_COMMAND.QUERY, "router", "log", "");

        HashMap<String, String>map = new HashMap<String, String>();
        map.put("level", "err");
        cmd.setQueryFields(map);

        storage.tell(cmd, ActorRef.noSender());

        try {
            TimeUnit.MINUTES.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("About to shut things down");

        system.terminate();
    }
}
