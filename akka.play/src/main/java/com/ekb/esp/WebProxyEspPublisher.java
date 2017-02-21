package com.ekb.esp;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.sas.esp.api.pubsub.dfESPclient;
import com.sas.esp.api.pubsub.dfESPclientHandler;
import com.sas.esp.api.server.ReferenceIMPL.dfESPdvString;
import com.sas.esp.api.server.ReferenceIMPL.dfESPevent;
import com.sas.esp.api.server.ReferenceIMPL.dfESPeventblock;
import com.sas.esp.api.server.ReferenceIMPL.dfESPschema;
import com.sas.esp.api.server.datavar;
import com.sas.esp.api.server.eventblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by ebrown on 2/14/2017.
 *
 *
 */
public class WebProxyEspPublisher extends UntypedActor {
    private final static long BLOCK_SIZE = 1000;
    private String espUri;
    private dfESPclientHandler clientHandler;
    private dfESPclient client;
    private dfESPschema schema;
    private ArrayList<dfESPevent>events;

    public WebProxyEspPublisher(String espUri) {
        log.info("Saving URL '" + espUri + "'");
        this.espUri = espUri;
    }

    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    /**
     * Connect to the ESP XML server
     * @throws Exception
     */
    @Override
    public void preStart() throws Exception {

        log.info("PRESTART for WebProxy ESP...");

        clientHandler = new dfESPclientHandler();
        clientHandler.init(Level.FINE);

        //TODO Do you really want to do this? I don't think so
        client = clientHandler.publisherStart(espUri, new DefaultCallbacks(), 0);

        if (client == null) {
            log.error("Unable to start ESP client handler failed");
            throw new RuntimeException("ESP start failure");
        }

        if (!clientHandler.connect(client)) {
            log.error("Unable to connect to the URI: " + espUri);
            throw new RuntimeException("Unable to connect to the client");
        } // if (!clientHandler.connect(client)) {

        log.info("Connected to '" + espUri + "' at this point. Getting the schema.");

        String schemaUrl = espUri + "?get=schema";
        ArrayList<String> schemaVector = clientHandler.queryMeta(schemaUrl);

        if (schemaVector == null) {

            log.error("Unable to get the schema for " + schemaUrl);

            throw new RuntimeException("ESP Publisher Schema query failed.");

        } // if (schemaVector == null) {

        schema = new dfESPschema(schemaVector.get(0));

        events = new ArrayList<dfESPevent>();
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void postStop() throws Exception {

        log.info("Stopping ESP connection");

        if (events.size() > 0) {
            dfESPeventblock eb = new dfESPeventblock(events,
                    eventblock.EventBlockType.ebt_NORMAL);
			/* Create event block. */
            // Log.debug(this,"====== Flushing remaining events in queue Events Size ("+events.size()+") publishing block! ===================");

            if (!clientHandler.publisherInject(client, eb)) {
                throw new RuntimeException("publisherInject failed.");
            }

            events.clear();
            clientHandler.stop(client, true);
        }
    }

    /**
     *
     * @param message
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof HashMap) {
            HashMap<String, Object>parts = (HashMap<String, Object>)message;

            ArrayList<datavar> data = new ArrayList<datavar>();

            data.add(new dfESPdvString(UUID.randomUUID().toString()));
            data.add(new dfESPdvString((String)parts.get("action")));
            data.add(new dfESPdvString((String)parts.get("reason")));
            data.add(new dfESPdvString((String)parts.get("protocol")));
            data.add(new dfESPdvString((String)parts.get("serverip")));
            data.add(new dfESPdvString((String)parts.get("requestmethod")));
            data.add(new dfESPdvString((String)parts.get("user")));
            data.add(new dfESPdvString((String)parts.get("hostname")));
            data.add(new dfESPdvString((String)parts.get("clientpublicIP")));
            data.add(new dfESPdvString((String)parts.get("refererURL")));

            dfESPevent event = new dfESPevent(schema, data, com.sas.esp.api.server.event.EventOpcodes.eo_INSERT, 0);
            event.setFlags(dfESPevent.EventFlags.ef_NORMAL.value);

            events.add(event);

            if (events.size() >= BLOCK_SIZE) {
                dfESPeventblock eb = new dfESPeventblock(events,
                        eventblock.EventBlockType.ebt_NORMAL);

				// Create event block.
                if (!clientHandler.publisherInject(client, eb)) {
                    throw new RuntimeException("publisherInject failed.");
                }

                events.clear();
            } // if (events.size() >= BLOCK_SIZE) {
        } // if (message instanceof HashMap) {
        else {
            unhandled(message);
        }
    }
}
