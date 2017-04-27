package com.bsc.cache;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by EdwinBrown on 4/26/2017.
 */
public class TheCacheTest {
    private static Logger log = LoggerFactory.getLogger(TheCacheTest.class);

    private static TheCache<String, String>cache;

    @BeforeClass
    public static void setup() {
        cache = new TheCache<String, String>(String.class, String.class, "myTest");

        log.info("Cache created");
    }

    @Test
    public void put() throws Exception {
        cache.put("lastName", "Brown");
        cache.put("firstName", "Edwin");
        cache.put("address", "1234 Main St");
        log.info("lastName: " + cache.get("lastName"));
        log.info("noKey: " + cache.get("noKey"));

        log.info("All: " + cache.getAll());
    }

//    @Test
//    public void get() throws Exception {
//        log.info("lastName: " + cache.get("lastName"));
//        log.info("noKey: " + cache.get("noKey"));
//    }
//
//    @Test
//    public void get1() throws Exception {
//    }
//
//    @Test
//    public void get2() throws Exception {
//    }
//
//    @Test
//    public void get3() throws Exception {
//    }
//
//    @Test
//    public void clear() throws Exception {
//    }
//
//    @Test
//    public void close() throws Exception {
//    }

    @AfterClass
    public static void tearDown() {
        log.info("Shutting it down");
        cache.close();
    }
}