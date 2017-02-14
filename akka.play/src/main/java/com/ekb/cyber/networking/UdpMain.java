package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.cyber.networking.debugging.Debugger;
import com.ekb.cyber.networking.filters.ContainsFilter;
import com.ekb.cyber.networking.parsers.AuthenticationParser;
import com.ekb.cyber.networking.udp.SyslogUdp;

import java.util.concurrent.TimeUnit;

/**
 * Created by ebrown on 2/9/2017.
 */
public class UdpMain {
    public static void main(String[] args) {
        try {

            int port;

            if (args.length >= 1) {
                port = Math.abs(Integer.parseInt(args[0]));
            }
            else {
                port = 2056;
            }

            ActorSystem system = ActorSystem.create("UdpNetwork");

            ActorRef debugger = system.actorOf(Props.create(Debugger.class), "OutputDebugger");
            ActorRef parser = system.actorOf(Props.create(AuthenticationParser.class, debugger), "Parser");
            ActorRef filter = system.actorOf(Props.create(ContainsFilter.class, "LogonType", parser), "Filter");
            ActorRef syslogUdp = system.actorOf(Props.create(SyslogUdp.class, port, filter), "SyslogUdp");

            TimeUnit.MINUTES.sleep(5L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
