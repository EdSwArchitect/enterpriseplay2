package com.ekb.akka.es;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ekb.akka.parse.RouterParser;

import java.util.HashMap;

/**
 * Formats the data for insert into Elastic Search before sending message to the storage object for insertion
 * Created by EdwinBrown on 12/27/2016.
 */
public class EsFormatter extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    private ActorRef storage;

    public EsFormatter() {
        storage = context().actorOf(Props.create(EsStorage.class), "ElasticSearch");
    }

    /**
     * Really, this is just a simple way to add another message to the chain. Makes the command for the storage
     * actor which will insert it
     * @param msg Message expecting to be a router parser object
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {

        log.info("Received: " + msg);

        if (msg instanceof RouterParser) {

            RouterParser parser = (RouterParser)msg;
            String textForES = parser.toJson();

            EsStorage.EsCmd cmd = null;

            if (parser.isBulk()) {
                cmd = new EsStorage.EsCmd(EsStorage.ES_COMMAND.BUFFER, "router", "log", textForES);
            } // if (parser.isBulk()) {
            else {
                cmd = new EsStorage.EsCmd(EsStorage.ES_COMMAND.INSERT, "router", "log", textForES);
            }

            storage.tell(cmd, self());
        } // if (msg instanceof RouterParser) {
        else {
            unhandled(msg);
        }

    }
}
