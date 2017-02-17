package com.ekb.cyber.networking.parsers;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ebrown on 2/10/2017.
 */
public class WebProxyParser extends UntypedActor {

    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /** the next actor in the chain */
    private ActorRef nextActor;

    /** pattern for parsing web proxy logs */
    private final Pattern pat = Pattern.compile("(\\d{4}\\-\\d{2}\\-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+reason=(\\w+)\\s+event_id=(\\d+)\\s+protocol=(\\w+)\\s+action=(\\w+)\\s+transactionsize=(\\d+)" +
            "\\s+responsesize=(\\d+)\\s+requestsize=(\\d+)\\s+urlcategory=(.+)\\s+serverip=([\\d\\.]+)\\s" +
            "clienttranstime=(\\d+)\\s+requestmethod=(\\w+)\\s+refererURL=(.+)useragent=(.*)product=(\\w+)\\s+" +
            "location=(.+)ClientIP=([\\d\\.]+)\\s+status=(.+)user=(.+)url=(.+)vendor=([\\w\\d]+)" +
            "\\s+hostname=(.+)clientpublicIP=([\\d\\.]+)\\s+threatcategory=(.+)threatname=(\\w+)\\s+" +
            "filetype=([\\w\\d]+)\\s+appname=(.+)pagerisk=(.+)department=(.+)urlsupercategory=(.+)" +
            "appclass=(.+)dlpengine=(.+)urlclass=(.+)threatclass=(.+)dlpdictionaries=(.+)fileclass=(.+)" +
            "bwthrottle=(.+)servertranstime=(.*)");

    /**
     * Constructor
     */
    public WebProxyParser() {
        log.debug("WebProxyParser created without a next actor");
    }
    /**
     *
     * @param nextActor Actor to send the hash map to
     */
    public WebProxyParser(ActorRef nextActor) {
        this.nextActor = nextActor;
        log.debug("WebProxyParser created with a next actor: " + nextActor);
    }

    /**
     *
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        log.debug("Message received: >>" + message + "<<");

        if (message instanceof String) {
            String record = (String) message;

            Matcher matcher = pat.matcher(record);

            if (matcher.matches()) {

                HashMap<String, String>bits = new HashMap<String, String>();

                bits.put("date", matcher.group(1).trim());
                bits.put("reason", matcher.group(2).trim());
                bits.put("protocol", matcher.group(4).trim());
                bits.put("action", matcher.group(5).trim());
                bits.put("transactionsize", matcher.group(6).trim());
                bits.put("responsesize", matcher.group(7).trim());
                bits.put("requestsize", matcher.group(8).trim());
                bits.put("urlcategory", matcher.group(9).trim());
                bits.put("serverip", matcher.group(10).trim());
                bits.put("clienttranstime", matcher.group(11).trim());
                bits.put("requestmethod", matcher.group(12).trim());
                bits.put("refererURL", matcher.group(13).trim());
                bits.put("useragent", matcher.group(14).trim());
                bits.put("ClientIP", matcher.group(17).trim());
                bits.put("status", matcher.group(18).trim());
                bits.put("user", matcher.group(19).trim());
                bits.put("vendor", matcher.group(20).trim());
                bits.put("hostname", matcher.group(21).trim());
                bits.put("clientpublicIP", matcher.group(22).trim());
                bits.put("threatcategory", matcher.group(23).trim());
                bits.put("threatname", matcher.group(24).trim());
                bits.put("filetype", matcher.group(25).trim());
                bits.put("appname", matcher.group(26).trim());
                bits.put("department", matcher.group(27).trim());
                bits.put("pagerisk", matcher.group(28).trim());
                bits.put("urlsupercategory", matcher.group(29).trim());
                bits.put("appclass", matcher.group(30).trim());
                bits.put("urlclass", matcher.group(31).trim());
                bits.put("threatclass", matcher.group(32).trim());
                bits.put("dlpdictionaries", matcher.group(33).trim());
                bits.put("fileclass", matcher.group(34).trim());
                bits.put("bwthrottle", matcher.group(35).trim());
                bits.put("servertranstime", matcher.group(36).trim());

                if (nextActor != null) {
                    nextActor.tell(bits, getSelf());
                }
                else {
                    log.info("Parser parsed: " + bits);
                }
            } // if (matcher.matches()) {
            else {
                log.error("Unable to parse: " + record);
            }
        }
        else {
            unhandled(message);
        }
    }
}
