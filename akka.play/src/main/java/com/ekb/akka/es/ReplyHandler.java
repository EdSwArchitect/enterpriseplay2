package com.ekb.akka.es;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.concurrent.Future;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by EdwinBrown on 1/1/2017.
 */
public class ReplyHandler extends UntypedActor {
    /** logger */

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /** actor */
    private ActorRef queryLogger;

    /**
     * Constuctor
     */
    public ReplyHandler() {
        queryLogger = context().actorOf(Props.create(EsStorage.class), "Query_Logger");
    }

    /**
     *
     * @param msg
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof String) {
            log.info("Received a reply from storage");
            log.info((String)msg);
        }
        else if (msg instanceof Integer) {
            log.info("Received object to cause the query");
            EsStorage.EsCmd cmd = new EsStorage.EsCmd(EsStorage.ES_COMMAND.QUERY, "router", "log", "");

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("level", "err");
            cmd.setQueryFields(map);
            queryLogger.tell(cmd, self());
        }
        else {
            unhandled(msg);
        }

//        final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
//
//        futures.add(ask(queryLogger, "myQuery", 1000L*30));
//
//        final Future<Iterable<Object>> aggregate = Futures.sequence(futures,
//                system.dispatcher());

    }
}
