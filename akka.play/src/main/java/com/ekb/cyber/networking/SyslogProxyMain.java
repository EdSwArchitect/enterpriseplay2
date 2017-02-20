package com.ekb.cyber.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import com.ekb.SyslogWebProxyApplication;

import java.net.InetAddress;
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
                hostName = InetAddress.getLocalHost().getHostName();
                port = 2057;
                espUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/Authentication_Query/Authentication";
            }

            ActorSystem system = ActorSystem.create("TcpNetwork");

            LoggingAdapter log = system.log();

            log.info("Hostname: " + hostName);
            log.info("Port: " + port);
            log.info("espUri: " + espUri);

            ActorRef webProxy = system.actorOf(Props.create(SyslogWebProxyApplication.class, hostName, port), "WebProxy");

            log.info("Starting the web proxy application...");


            webProxy.tell(SyslogWebProxyApplication.COMMAND.START, null);

//            ActorRef debugger = system.actorOf(Props.create(Debugger.class), "OutputDebugger");
//
//            ActorRef parser = system.actorOf(Props.create(WebProxyParser.class, debugger), "WebProxyParser");
//
//            ActorRef buffer = system.actorOf(Props.create(Buffering.class, parser), "Buffering");
//
//            ActorRef syslogUdp = system.actorOf(Props.create(SyslogTcp.class, hostName, port, buffer), "SyslogTcp");

            // zz-ed-vm01.dev.cyber.sas.com:9080/SASESP

            TimeUnit.MINUTES.sleep(5L);

            webProxy.tell(SyslogWebProxyApplication.COMMAND.STOP, null);

            log.info("Waiting 2 minutes to terminate");

            TimeUnit.MINUTES.sleep(2L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
