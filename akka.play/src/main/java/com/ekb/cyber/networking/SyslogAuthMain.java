package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import com.ekb.SyslogAuthApplication;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by ebrown on 2/9/2017.
 */
public class SyslogAuthMain {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {

            int port;
            String espUri;
            String hostName;

            if (args.length >= 3) {
                hostName = args[0];
                port = Math.abs(Integer.parseInt(args[1]));
                espUri = args[2];
            }
            else {
                hostName = InetAddress.getLocalHost().getHostName();
                port = 2056;
                espUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/Authentication_Query/Authentication";
            }

            ActorSystem system = ActorSystem.create("UdpNetwork");

            LoggingAdapter log = system.log();

            ActorRef auth = system.actorOf(Props.create(SyslogAuthApplication.class, hostName, port, espUri), "Auth");

            log.info("Starting the authentication application...");

            auth.tell(SyslogAuthApplication.COMMAND.START, null);


//            ActorRef esp = system.actorOf(Props.create(EspPublisher.class, espUri), "ESP");
//            ActorRef parser = system.actorOf(Props.create(AuthenticationParser.class, esp), "Parser");
//            ActorRef filter = system.actorOf(Props.create(ContainsFilter.class, "LogonType", parser), "Filter");
//
//            ActorRef metrics = null;
//
//            if (args.length >= 2) {
//                if (Boolean.parseBoolean(args[1])) {
//                    metrics = system.actorOf(Props.create(MetricsListener.class), "Metrics");
//                }
//            }
//
//            ActorRef syslogUdp = system.actorOf(Props.create(SyslogUdp.class, port, filter), "SyslogUdp");

            // zz-ed-vm01.dev.cyber.sas.com:9080/SASESP

            TimeUnit.MINUTES.sleep(5L);

            log.info("Delay done. shut it down");

            auth.tell(SyslogAuthApplication.COMMAND.STOP, null);

            log.info("Delay 2 minutes to terminate");

            TimeUnit.MINUTES.sleep(2L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
