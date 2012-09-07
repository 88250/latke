/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.latke.cache.bae;

import com.baidu.bae.api.memcache.BaeMemcachedClient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.util.Serializer;

/**
 * Simple warper of <a href="http://developer.baidu.com/wiki/index.php?title=帮助文档/云环境/JAVA服务列表/Cache">
 * Baidu App Engine memcache service</a>.
 * 
 * <p>
 *   <b>Note</b>:
 *   <ul>
 *     <li>Unsupported statistic yet</li>
 *   </ul>
 * </p>
 *
 * @param <K> the key of an object
 * @param <V> the type of objects
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 7, 2012
 */
public final class Memcache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Memcache.class.getName());
    /**
     * Name of this cache.
     */
    private String name;
    /**
     * BAE memcached client.
     */
    private BaeMemcachedClient baeMemcachedClient;
    /**
     * Keys.
     */
    private Set<String> keys = Collections.synchronizedSet(new HashSet<String>());

    /**
     * Constructs a memcache with the specified name.
     *
     * @param name the specified name
     */
    public Memcache(final String name) {
        this.name = name;
        baeMemcachedClient = new BaeMemcachedClient();
    }

    /**
     * Gets the name of this cache.
     *
     * @return name of this cache
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean contains(final K key) {
        return keys.contains(key.toString());
    }

    @Override
    public void put(final K key, final V value) {
        if (null == key) {
            throw new IllegalArgumentException("The specified key can not be null!");
        }

        if (null == value) {
            throw new IllegalArgumentException("The specified value can not be null![key=" + key + "]");
        }

        try {
            baeMemcachedClient.set(key.toString(), value);
            keys.add(key.toString());
        } catch (final Exception e) {
            try {
                LOGGER.log(Level.WARNING, "Can not put memcache[key=" + key
                                          + ", valueSize=" + Serializer.serialize((Serializable) value).length, e);
            } catch (final Exception ex) {
                LOGGER.log(Level.SEVERE, " Serializes failed", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * <b>Note</b>: Dose <em>NOT</em> support async put at present, calls this method is equivalent to call 
     * {@link #put(java.io.Serializable, java.io.Serializable)}.
     * </p>
     */
    @Override
    public void putAsync(final K key, final V value) {
        put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(final K key) {
        if (null == key) {
            return null;
        }

        return (V) baeMemcachedClient.get(key.toString());
    }

    @Override
    public long inc(final K key, final long delta) {
        if (null == key) {
            throw new IllegalArgumentException("The specified key can not be null!");
        }

        if (!baeMemcachedClient.keyExists(key.toString())) {
            baeMemcachedClient.set(key.toString(), 1L);
            keys.add(key.toString());
        }

        return baeMemcachedClient.incr(key.toString(), delta);
    }

    @Override
    public void remove(final K key) {
        baeMemcachedClient.delete(key.toString());
        keys.remove(key.toString());
    }

    @Override
    public void remove(final Collection<K> keys) {
        for (final K k : keys) {
            remove(k);
        }
    }

    @Override
    public void removeAll() {
        for (final String key : keys) {
            baeMemcachedClient.delete(key.toString());
            keys.remove(key.toString());
        }
    }

    @Override
    public void setMaxCount(final long maxCount) {
    }

    @Override
    public long getMaxCount() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getHitCount() {
        return -1;
    }

    @Override
    public long getMissCount() {
        return -1;
    }

    @Override
    public long getPutCount() {
        return getCachedCount();
    }

    @Override
    public long getCachedBytes() {
        return -1;
    }

    @Override
    public long getHitBytes() {
        return -1;
    }

    @Override
    public long getCachedCount() {
        return -1;
    }

    @Override
    public void collect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
