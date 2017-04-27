package com.bsc.cache;

import java.util.Map;
import java.util.Set;

/**
 * Created by EdwinBrown on 4/26/2017.
 */
public interface MyCache<K, V> {
    /**
     *
     * @param key
     * @param value
     */
    public void put(K key, V value);

    /**
     *
     * @param key
     * @return
     */
    public V get(K key);

    /**
     *
     * @return
     */
    public Map<K, V> getAll();

    /**
     *
     * @param keys
     * @return
     */
    public Map<K, V> get(Set<K>keys);

    /**
     *
     * @param key
     * @return
     */
    public Map<K, V> get(K... key);

    /**
     * Clear the cache
     */
    public void clear();

    /**
     * Close the cache
     */
    public void close();
}
