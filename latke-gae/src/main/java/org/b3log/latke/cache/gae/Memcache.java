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
package org.b3log.latke.cache.gae;

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.util.Serializer;

/**
 * Simple warper of <a href="http://code.google.com/appengine/docs/java/memcache/">
 * Google App Engine memcache service</a>.
 * 
 * <p>
 *   <b>Note</b>:
 *   <ul>
 *     <li>Invoking {@link #removeAll()} will clear all caches.</li>
 *     <li>Statistics does not respect caches, this will return statistic states 
 *         sum for all caches.</li>
 *   </ul>
 * </p>
 *
 * @param <K> the key of an object
 * @param <V> the type of objects
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.6, Dec 3, 2011
 */
public final class Memcache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Memcache.class.getName());
    /**
     * Memcache service.
     */
    private MemcacheService memcacheService;
    /**
     * Asynchronous memcache service.
     */
    private AsyncMemcacheService asyncMemcacheService;
    /**
     * Name of this cache.
     */
    private String name;
    /**
     * Integer value for true flag.
     */
    private static final int TRUE_INT = 49;
    /**
     * Integer value for false flag.
     */
    private static final int FALSE_INT = 48;

    /**
     * Constructs a memcache with the specified name.
     *
     * @param name the specified name
     */
    public Memcache(final String name) {
        this.name = name;

        memcacheService = MemcacheServiceFactory.getMemcacheService(name);
        asyncMemcacheService = MemcacheServiceFactory.getAsyncMemcacheService(name);
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
        return memcacheService.contains(key);
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
            memcacheService.put(key, value);
        } catch (final Exception e) {
            try {
                LOGGER.log(Level.WARNING, "Can not put memcache[key=" + key
                                          + ", valueSize=" + Serializer.serialize((Serializable) value).length, e);
            } catch (final Exception ex) {
                LOGGER.log(Level.SEVERE, " Serializes failed", ex);
            }
        }
    }

    @Override
    public void putAsync(final K key, final V value) {
        if (null == key) {
            throw new IllegalArgumentException("The specified key can not be null!");
        }

        if (null == value) {
            throw new IllegalArgumentException("The specified value can not be null![key=" + key + "]");
        }

        try {
            asyncMemcacheService.put(key, value);
        } catch (final Exception e) {
            try {
                LOGGER.log(Level.WARNING, "Can not put async memcache[key=" + key
                                          + ", valueSize=" + Serializer.serialize((Serializable) value).length, e);
            } catch (final Exception ex) {
                LOGGER.log(Level.SEVERE, " Serializes failed", ex);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(final K key) {
        if (null == key) {
            return null;
        }

        return (V) memcacheService.get(key);
    }

    @Override
    public long inc(final K key, final long delta) {
        if (null == key) {
            throw new IllegalArgumentException("The specified key can not be null!");
        }

        if (!memcacheService.contains(key)) {
            memcacheService.put(key, 1L);
        }

        return memcacheService.increment(key, delta);
    }

    @Override
    public void remove(final K key) {
        memcacheService.delete(key);
    }

    @Override
    public void remove(final Collection<K> keys) {
        memcacheService.deleteAll(keys);
    }

    @Override
    public void removeAll() {
        memcacheService.clearAll(); // Will clear in all namespaces
        LOGGER.finest("Clear all caches");
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
        final Stats statistics = memcacheService.getStatistics();
        if (null != statistics) {
            return statistics.getHitCount();
        }

        return -1;
    }

    @Override
    public long getMissCount() {
        final Stats statistics = memcacheService.getStatistics();
        if (null != statistics) {
            return statistics.getMissCount();
        }

        return -1;
    }

    @Override
    public long getPutCount() {
        return getCachedCount();
    }

    @Override
    public long getCachedBytes() {
        final Stats statistics = memcacheService.getStatistics();
        if (null != statistics) {
            return statistics.getTotalItemBytes();
        }

        return -1;
    }

    @Override
    public long getHitBytes() {
        final Stats statistics = memcacheService.getStatistics();
        if (null != statistics) {
            return statistics.getBytesReturnedForHits();
        }

        return -1;
    }

    @Override
    public long getCachedCount() {
        final Stats statistics = memcacheService.getStatistics();
        if (null != statistics) {
            return statistics.getItemCount();
        }

        return -1;
    }

    @Override
    public void collect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
