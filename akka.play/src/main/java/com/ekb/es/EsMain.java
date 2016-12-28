package com.ekb.es;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main class to kick things off for searching elastic search
 * Created by EdwinBrown on 12/22/2016.
 */
public class EsMain {
    public static Logger log = LogManager.getLogger(EsMain.class);

    public static void main(String ...args) {
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            SearchResponse sr = client.prepareSearch()
                    .setQuery(QueryBuilders.matchQuery("message", "myProduct"))
                    .addAggregation(AggregationBuilders.terms("top_10_states")
                            .field("state").size(10))
                    .execute().actionGet();

            SearchHit[] hits = sr.getHits().getHits();

            log.info("Number of hits: " + hits.length);

            client.close();
        }
        catch(NullPointerException npe) {
            npe.printStackTrace();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
    }
}
