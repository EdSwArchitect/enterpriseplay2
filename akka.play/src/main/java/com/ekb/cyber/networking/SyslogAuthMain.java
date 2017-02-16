package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.cyber.networking.filters.ContainsFilter;
import com.ekb.cyber.networking.metrics.MetricsListener;
import com.ekb.cyber.networking.parsers.AuthenticationParser;
import com.ekb.cyber.networking.udp.SyslogUdp;
import com.ekb.esp.EspPublisher;

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

            if (args.length >= 2) {
                port = Math.abs(Integer.parseInt(args[1]));
                espUri = args[0];
            }
            else {
                port = 2056;
                espUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/Authentication_Query/Authentication";
            }

            ActorSystem system = ActorSystem.create("UdpNetwork");

            ActorRef esp = system.actorOf(Props.create(EspPublisher.class, espUri), "ESP");
//            ActorRef debugger = system.actorOf(Props.create(Debugger.class), "OutputDebugger");
            ActorRef parser = system.actorOf(Props.create(AuthenticationParser.class, esp), "Parser");
            ActorRef filter = system.actorOf(Props.create(ContainsFilter.class, "LogonType", parser), "Filter");

            ActorRef metrics = null;

            if (args.length >= 2) {
                if (Boolean.parseBoolean(args[1])) {
                    metrics = system.actorOf(Props.create(MetricsListener.class), "Metrics");
                }
            }

            ActorRef syslogUdp = system.actorOf(Props.create(SyslogUdp.class, port, filter), "SyslogUdp");

            // zz-ed-vm01.dev.cyber.sas.com:9080/SASESP

            TimeUnit.MINUTES.sleep(5L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
