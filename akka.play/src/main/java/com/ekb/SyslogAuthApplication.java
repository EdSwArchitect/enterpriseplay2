package com.ekb;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ekb.cyber.networking.filters.ContainsFilter;
import com.ekb.cyber.networking.parsers.AuthenticationParser;
import com.ekb.cyber.networking.udp.SyslogUdp;
import com.ekb.esp.AuthEspPublisher;

/**
 * Created by ebrown on 2/17/2017.
 */
public class SyslogAuthApplication extends UntypedActor {
    /**
     * Messages recognized by the application
     */
    public static enum COMMAND {
        START, STOP, PAUSE, METRICS
    }

    ;

    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /**
     * host name
     */
    private String hostName;
    /**
     * port
     */
    private int port;
    /**
     * esp uri
     */
    private String espUri;

    ActorRef esp; // = system.actorOf(Props.create(AuthEspPublisher.class, espUri), "ESP");
    ActorRef parser; // = system.actorOf(Props.create(AuthenticationParser.class, esp), "Parser");
    ActorRef filter; // = system.actorOf(Props.create(ContainsFilter.class, "LogonType", parser), "Filter");
    ActorRef syslogUdp; // = system.actorOf(Props.create(SyslogUdp.class, port, filter), "SyslogUdp");

    public SyslogAuthApplication(String hostName, int port, String espUri) {
        this.hostName = hostName;
        this.port = port;
        this.espUri = espUri;
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SyslogAuthApplication.COMMAND) {

            SyslogAuthApplication.COMMAND whatToDo = (SyslogAuthApplication.COMMAND) message;

            log.info("****** COMMAND *****: " + whatToDo);

            switch (whatToDo) {
                case START:
                    esp = context().actorOf(Props.create(AuthEspPublisher.class, espUri), "ESP");

                    parser = context().actorOf(Props.create(AuthenticationParser.class, esp), "Parser");

                    filter = context().actorOf(Props.create(ContainsFilter.class, "LogonType", parser), "Filter");

                    syslogUdp = context().actorOf(Props.create(SyslogUdp.class, port, filter), "SyslogUdp");

                    break;
                case PAUSE:
                    log.info("Command: " + whatToDo + " is not implemented");
                    break;
                case STOP:
                    stopTheKids();
                    break;
                case METRICS:
                    log.info("Command: " + whatToDo + " is not implemented");
                    break;
            }
        } else {
            log.error("Got unexpected message: " + message);
            unhandled(message);
        }

    }

    /**
     *
     */
    private void stopTheKids() {
        log.info("Stopping the children");
        context().stop(syslogUdp);
        context().stop(filter);
        context().stop(parser);
        context().stop(esp);
    }

}
