package com.ekb.akka.es;

import akka.actor.OneForOneStrategy;
import akka.actor.ReceiveTimeout;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import akka.japi.Function;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import scala.concurrent.duration.Duration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AKKA actor class to access Elastic Search. I'm just learning stuff.
 * Created by EdwinBrown on 12/22/2016.
 */
public class EsStorage extends UntypedActor {
    /** logger */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /** elasticsearch client */
    private TransportClient client;
    /** buffer size for buffering requests to elasticsearch */
    private final static int BUFFER_SIZE = 100;
    /** buffer counter */
    private int counter = 0;
    /** buffer request */
    private BulkRequestBuilder brb;

    /** enumeration */
    public static enum ES_COMMAND { QUERY, INSERT, BUFFER, DELETE };

    /** encapsulates the elasticsearch command */
    public static class EsCmd {
        private ES_COMMAND command;
        private String index;
        private String document;
        private String json;
        private Map<String, String> queryFields;

        private EsCmd() {
        }

        /**
         * Constructor
         * @param command The ElastcSearch command
         * @param index The index
         * @param document The document type
         * @param json The json command
         */
        public EsCmd(ES_COMMAND command, String index, String document, String json) {
            this.command = command;
            this.document = document;
            this.index = index;
            this.json = json;
        }

        /**
         * Get the command
         * @return One of the enumeration
         */
        public ES_COMMAND getCommand() {
            return command;
        }

        /**
         * Get the ElasticSearch index
         * @return The index
         */
        public String getIndex() {
            return index;
        }

        /**
         * Get the ElasticSearch document type
         * @return The document type
         */
        public String getDocument() {
            return document;
        }

        /**
         * Get the query fields
         * @return
         */
        public Map<String, String> getQueryFields() {
            return queryFields;
        }

        public void setQueryFields(Map<String, String>queryFields) {
            this.queryFields = queryFields;
        }

        /**
         * The JSON for the insert
         * @return
         */
        public String getJson() {
            return json;
        }

        @Override
        public String toString() {
            return "EsCmd{" +
                    "command=" + command +
                    ", index='" + index + '\'' +
                    ", document='" + document + '\'' +
                    ", queryFields='" + queryFields + '\'' +
                    ", json='" + json + '\'' +
                    '}';
        }
    }

    /**
     * Message received for the actor. If one of the commands, performs the ElasticSearch operation. For bulk requests,
     * a timer is set for 30 seconds. At the expiration of 30 seconds, if there is any data to be inserted, the left
     * over data is inserted.
     * @param message The message received.
     * @throws Throwable
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof EsCmd) {
            int status = -1;
            EsCmd cmd = (EsCmd)message;

            log.info(cmd.getCommand().name() + " received for index '" + cmd.getIndex() + "'");
            log.info(cmd.getJson());

            switch(cmd.getCommand()) {
                case DELETE:

                    IndicesAdminClient admin = client.admin().indices();
                    DeleteIndexRequestBuilder dirb = admin.prepareDelete(cmd.getIndex());

                    DeleteIndexResponse dir = dirb.get();

                    status = dir.isAcknowledged() ? 1 : 0;
                    this.getSender().tell("Results: " + status, this.getSelf());

                    break;
                case INSERT:
                    IndexRequestBuilder irb = client.prepareIndex(cmd.getIndex(), cmd.getDocument()).setSource(cmd
                            .getJson());

                    log.info("IRB: " + irb.toString());

                    IndexResponse resp = irb.get();

                    status = resp.status().getStatus();
                    this.getSender().tell("Results: " + status, this.getSelf());
                    break;
                case BUFFER:
                    if (brb == null) {
                        brb = client.prepareBulk();
                    }

                    // set the timeout for receiving the next message
                    context().setReceiveTimeout(Duration.create(30, TimeUnit.SECONDS));

                    brb.add(client.prepareIndex(cmd.getIndex(), cmd.getDocument()).setSource(cmd.getJson()));

                    if (++counter >= BUFFER_SIZE) {
                        BulkResponse br = brb.get();

                        status = br.hasFailures() ? 400 : 200;

                        counter = 0;
                        brb = client.prepareBulk();
                    } // if (++counter >= BUFFER_SIZE) {
                    else {
                        status = 0;
                    }
                    this.getSender().tell("Results: " + status, this.getSelf());
                    break;
                case QUERY:
                    SearchRequestBuilder srb =
                            client.prepareSearch(cmd.getIndex());

//                    client.prepareSearch(cmd.getIndex()) /*.setTypes(cmd.getDocument())*/.setSearchType(SearchType
//                            .QUERY_AND_FETCH);
//
//                    Map<String, String>qf = cmd.getQueryFields();
//
//                    if (qf != null) {
//                        for (String field : qf.keySet()) {
//                            TermQueryBuilder tqb = QueryBuilders.termQuery(field, qf.get(field));
//
//                            srb = srb.setQuery(tqb);
//                        }
//                    }

                    srb = srb.setFrom(0).setSize(1000);

                    log.info("srb: " + srb);

                    SearchResponse sr = srb.get();

                    log.info("search response: " +sr.status().name() + " - " + sr.status().getStatus() );

                    SearchHit[] hits = sr.getHits().hits();

                    StringBuffer results = new StringBuffer("Number of hits: ").append(hits.length).append('\n');

                    for (SearchHit hit : hits) {
                        results.append(hit.getSourceAsString()).append('\n');
                    }

                    // give the results back to the sender
                    getSender().tell(results.toString(), getSelf());
                    break;

            }
        } // if (message instanceof EsCmd) {
        else if (message instanceof ReceiveTimeout) {
            ReceiveTimeout rt = (ReceiveTimeout)message;

            log.info("Received timeout message");

            if (brb != null && brb.numberOfActions() > 0) {
                log.info("Bulk request has " + brb.numberOfActions() + " requests about to be sent.");

                BulkResponse br = brb.get();
                int status = br.hasFailures() ? 400 : 200;

                counter = 0;
                brb = client.prepareBulk();
                this.getSender().tell("Results: " + status, this.getSelf());
            }
        }
        else {
            log.warning("Unrecognized message: " + message);
            this.unhandled(message);
        }
    }

    /**
     * The Actor supervisor strategy. Basically, stop processing if there is an ElasticSearch or network error
     */
    private static SupervisorStrategy strategy = new OneForOneStrategy(3,
            Duration.create("5 seconds"), new Function<Throwable, SupervisorStrategy.Directive>() {

        @Override
        public SupervisorStrategy.Directive apply(Throwable throwable) throws Exception {

            if (throwable instanceof UnknownHostException) {

                throwable.printStackTrace();

                SupervisorStrategy.stop();
            }
            if (throwable instanceof ElasticsearchException) {

                throwable.printStackTrace();

                return SupervisorStrategy.stop();
            }
            else {
                return SupervisorStrategy.escalate();
            }
        }
    });

    /**
     * On pre start, connect to the local host ElasticSearch index
     * @throws Exception
     */
    @Override
    public void preStart() throws Exception {
        try {
            log.info("prestart");
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException uhe) {
            log.warning("UnknownHostException", uhe);
        }
    }

    /**
     * At the stopping of the application, close the ElasticSearch connection
     * @throws Exception
     */
    @Override
    public void postStop() throws Exception {
        log.info("postStop");
        client.close();
    }

    /**
     * The supervisor escalation strategy
     * @return
     */
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
