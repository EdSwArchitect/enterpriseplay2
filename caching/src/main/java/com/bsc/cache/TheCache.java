package com.bsc.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by EdwinBrown on 4/26/2017.
 */
public class TheCache<K, V> implements MyCache<K, V> {
    private Logger log = LoggerFactory.getLogger(TheCache.class);
    private Class<K> keyClass;
    private Class<V> valueClass;

    private CacheManager cacheManager;
    private Cache cache;
    private String cacheName;

    /**
     *
     */
    public TheCache(Class<K> keyClass, Class<V> valueClass, String cacheName) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.cacheName = cacheName;

        cacheManager
                = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(cacheName,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(keyClass, valueClass,
                                ResourcePoolsBuilder.heap(10)))
                .build();
        cacheManager.init();
        cache = cacheManager.getCache(cacheName, keyClass, valueClass);
    }


    /**
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        if (cache != null) {
            cache.put(key, value);
        }
    }

    /**
     * @param key
     * @return
     */
    @Override
    public V get(K key) {
        V value = null;

        if (cache != null) {
            value = (V) cache.get(key);
        }

        return value;
    }

    /**
     * @return
     */
    @Override
    public Map<K, V> getAll() {
        Map<K, V> map = new HashMap<K, V>();

        Iterator iterator = cache.iterator();

        while (iterator.hasNext()) {

            Cache.Entry<K, V> o = (Cache.Entry<K, V>)iterator.next();

            map.put(o.getKey(), o.getValue());
        } // while (iterator.hasNext()) {

        return map;
    }

    /**
     * @param keys
     * @return
     */
    @Override
    public Map<K, V> get(Set<K> keys) {
        Map<K, V> map = null;

        if (cache != null) {
            map = cache.getAll(keys);
        }

        return map;
    }

    /**
     * @param keys
     * @return
     */
    @Override
    public Map<K, V> get(K... keys) {
        Map<K, V> map = null;

        if (keys != null) {
            Set<K> keyset = new HashSet<K>();

            keyset.addAll(Arrays.asList(keys));
            map = cache.getAll(keyset);
        }

        return map;
    }

    /**
     * Clear the cache
     */
    @Override
    public void clear() {
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * Close the cache
     */
    @Override
    public void close() {
        cacheManager.close();
        cache = null;

    }
}
