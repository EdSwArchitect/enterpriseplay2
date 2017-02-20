package com.ekb;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ekb.cyber.networking.debugging.Debugger;
import com.ekb.cyber.networking.parsers.WebProxyParser;
import com.ekb.cyber.networking.tcp.Buffering;
import com.ekb.cyber.networking.tcp.SyslogTcp;

/**
 * The Syslog application for WebProxy. This is the parent actor that creates children to handle the processing.
 * Schweet.
 * Created by ebrown on 2/17/2017.
 */
public class SyslogWebProxyApplication extends UntypedActor {
    /**
     * Messages recognized by the application
     */
    public static enum COMMAND { START, STOP, PAUSE, METRICS };

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** host name */
    private String hostName;
    /** port */
    private int port;

    /** debugging only */
    private ActorRef debugger;
    /** parser */
    ActorRef parser;
    /** buffering component */
    ActorRef buffer;
    /** networking */
    ActorRef syslogUdp;

    /**
     *
     * @param hostName
     * @param port
     */
    public SyslogWebProxyApplication(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * I don't know what to do here
     * @throws Exception
     */
    @Override
    public void preStart() throws Exception {
        super.preStart();
    }

    /**
     * Tell all of the children to stop
     * @throws Exception
     */
    @Override
    public void postStop() throws Exception {
        stopTheKids();
    }

    /**
     * Expect the command of what to do and then do it.
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof COMMAND) {
            COMMAND whatToDo = (COMMAND)message;

            log.info("****** COMMAND *****: " + whatToDo);

            switch(whatToDo) {
                case START:
                    debugger = context().actorOf(Props.create(Debugger.class), "OutputDebugger");

                    parser = context().actorOf(Props.create(WebProxyParser.class, debugger), "WebProxyParser");

                    buffer = context().actorOf(Props.create(Buffering.class, parser), "Buffering");

                    syslogUdp = context().actorOf(Props.create(SyslogTcp.class, hostName, port, buffer), "SyslogTcp");

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
        }
        else {
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
        context().stop(buffer);
        context().stop(parser);
        context().stop(debugger);
    }
}
