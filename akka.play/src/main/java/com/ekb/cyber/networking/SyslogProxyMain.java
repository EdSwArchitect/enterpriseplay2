package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.cyber.networking.debugging.Debugger;
import com.ekb.cyber.networking.parsers.WebProxyParser;
import com.ekb.cyber.networking.tcp.SyslogTcp;

import java.util.concurrent.TimeUnit;

/**
 * Created by ebrown on 2/9/2017.
 */
public class SyslogProxyMain {
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
                hostName = "localhost";
                port = 2057;
                espUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/Authentication_Query/Authentication";
            }

            ActorSystem system = ActorSystem.create("TcpNetwork");

            system.log().info("Hostname: " + hostName);
            system.log().info("Port: " + port);
            system.log().info("espUri: " + espUri);

//            ActorRef esp = system.actorOf(Props.create(EspPublisher.class, espUri), "ESP");
            ActorRef debugger = system.actorOf(Props.create(Debugger.class), "OutputDebugger");

            ActorRef parser = system.actorOf(Props.create(WebProxyParser.class, debugger), "WebProxyParser");

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

//            ActorRef syslogUdp = system.actorOf(Props.create(SyslogTcpClient.class, hostName, port), "SyslogTcp");
            ActorRef syslogUdp = system.actorOf(Props.create(SyslogTcp.class, hostName, port, parser), "SyslogTcp");

            // zz-ed-vm01.dev.cyber.sas.com:9080/SASESP

            TimeUnit.MINUTES.sleep(5L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
