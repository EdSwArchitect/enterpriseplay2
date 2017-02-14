package com.ekb.cyber.networking.parsers;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ekb.cyber.networking.udp.UdpServer;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ebrown on 2/10/2017.
 */
public class AuthenticationParser extends UntypedActor {

    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /**
     * pattern for parsing authentication
     */
    private final Pattern pattern =
            Pattern.compile("<\\d*>(\\w{3} \\d{1,2} \\d{2}:\\d{2}:\\d{2}) ([\\w\\d\\.]+) Time:(\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2})\\, (.*) User:(.*), IPAddr:(.*)");

    /**
     * parser for syslog date
     */
    private final SimpleDateFormat syslogSdf = new SimpleDateFormat("MMM dd HH:mm:ss");
    /**
     * parser for login date
     */
    private final SimpleDateFormat loginSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * the next actor
     */
    private ActorRef nextActor;

    /**
     * @param nextActor Next actor to handle the data
     */
    public AuthenticationParser(ActorRef nextActor) {
        this.nextActor = nextActor;
    }

    /**
     * @param nextActor
     * @return
     */
    public static Props props(ActorRef nextActor) {

        return Props.create(AuthenticationParser.class, nextActor);
    }

    /**
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
//        log.info("Message received: " + message);

        if (message instanceof String) {
            String syslog = (String) message;

            syslog = syslog.trim();

            try {
                Matcher matcher = pattern.matcher(syslog);

                if (matcher.matches()) {
                    String syslogDate = matcher.group(1);
                    String serverName = matcher.group(2);
                    String loginDate = matcher.group(3);
                    String user = matcher.group(5);
                    String ipAddress = matcher.group(6);

                    HashMap<String, Object> components = new HashMap<String, Object>();

                    components.put("syslogDate", syslogSdf.parse(syslogDate).getTime());
                    components.put("serverName", serverName);
                    components.put("loginDate", loginSdf.parse(loginDate).getTime());
                    components.put("user", user);
                    components.put("ipAddress", ipAddress);

                    nextActor.tell(components, getSelf());
                }
            } catch (IllegalStateException ise) {
                log.warning("Failed on string '" + syslog + "'");
            }

        } // if (message instanceof String) {
        else {
            log.warning("Unknown message for parser: " + message);
            unhandled(message);
        }
    }
}
