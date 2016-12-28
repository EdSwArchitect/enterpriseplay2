package com.ekb.akka.es;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.akka.parse.ParseLog;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Class to kick off the Actor system
 * Created by EdwinBrown on 12/22/2016.
 */
public class MyMain {
    public static void main(String... args) {
        System.out.println("Startup");
        ActorSystem system = ActorSystem.create("EsPlay");
//        ActorRef esStorage = system.actorOf(Props.create(EsStorage.class), "EsStorage");
//
//        esStorage.tell("Hi Ed", ActorRef.noSender());

        ActorRef parser = system.actorOf(Props.create(ParseLog.class), "RouterParser");

        File file = new File("C:\\data\\SystemLog.txt");

//        String msg = "Dec 23 14:25:05 2016 local1.info<142> IGMP: leave group 224.0.1.60 on if 2,  total number of " +
//                "leave requests 1126";

        //parser.tell(msg, ActorRef.noSender());

        parser.tell(file, ActorRef.noSender());

        try {
            TimeUnit.MINUTES.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("About to shut things down");

        system.terminate();
    }
}
