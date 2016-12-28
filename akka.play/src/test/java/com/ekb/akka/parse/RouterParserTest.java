package com.ekb.akka.parse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by EdwinBrown on 12/23/2016.
 */
public class RouterParserTest {
    /**
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {

    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void parse() throws Exception {
        String LOG = "Dec 23 14:25:05 2016 local1.info<142> IGMP: leave group 224.0.1.60 on if 2,  total number of " +
                "leave requests 1126";

        RouterParser parser = new RouterParser();

        Assert.assertTrue("Failed parsing this record: '" + LOG + "'", parser.parse(LOG));

    }

    @Test
    public void parse2ndType() throws Exception {
        String LOG = "Dec 20 14:48:55 2016 local1.err<139> named[3304]: zone localrp/IN/internal-clients: zone serial" +
                " (2008122601) unchanged. zone may fail to transfer to slaves.";
        RouterParser parser = new RouterParser();

        Assert.assertTrue("Failed parsing this record: '" + LOG + "'", parser.parse(LOG));
    }

}