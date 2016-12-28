package com.ekb.akka.parse;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.ekb.akka.es.EsFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Actor to parse the incoming data. If a string, assumes it's a log record. If it's a file, assumes it's a file
 * that contains the log records
 * Created by EdwinBrown on 12/27/2016.
 */
public class ParseLog extends UntypedActor {
    /**
     * Logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /**
     * Child actor for formatting the data for ElasticSearch
     */
    private ActorRef esFormatter;

    /**
     * Constructor
     */
    public ParseLog() {
        esFormatter = getContext().actorOf(Props.create(EsFormatter.class), "EsFormatter");
    }


    /**
     * If a string, assume it's log record and attempt to parse if. If parsed, send to the next actor.
     * If a file, assume it's a file of log records. Read the file parse it, and send the next actor
     * @param msg
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {
        RouterParser parser = new RouterParser();

        if (msg instanceof String) {
            log.info("Received a string. assuming it's a log record");
            parser.setBulk(false);
            parseIt(parser, (String)msg, false);
        } // if (msg instanceof String) {
        else if (msg instanceof File) {
            log.info("Received a File class message");
            File dataFile = (File)msg;

            FileInputStream fis = new FileInputStream(dataFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String record;
            parser.setBulk(true);

            while ((record = in.readLine()) != null) {
                parseIt(parser, record, true);
            } // while ((record = in.readLine()) != null) {

            in.close();
        } // else if (msg instanceof File) {
        else {
            log.error("Not expecting message " + msg.getClass().getName() + " routing to error");
            unhandled(msg);
        }
    }

    /**
     * Parse the record
     * @param parser The parser
     * @param record the log record
     * @param bulk if true, make it a bulk insert, otherwise it's an insert
     */
    private void parseIt(RouterParser parser, String record, boolean bulk) {
        if (parser.parse(record)) {
            // send on to the next person
            esFormatter.tell(parser, getSelf());
        } // if (parser.parse(record)) {
        else {
            log.warning("Unable to parse record: " + record);
        }
    }
}
