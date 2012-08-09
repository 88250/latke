/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.cache.local.memory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.local.util.DoubleLinkedMap;
import org.b3log.latke.util.Serializer;

/**
 * This is a Least Recently Used (LRU) pure memory cache. This cache use a 
 * thread-safe {@link DoubleLinkedMap} to hold the objects, and 
 * the least recently used objects will be moved to the end of the list and to
 * remove by invoking {@link #collect()} method. 
 *
 * @param <K> the type of the key of the object
 * @param <V> the type of the objects
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.9, Dec 3, 2011
 */
public final class LruMemoryCache<K extends Serializable, V extends Serializable>
        extends AbstractMemoryCache<K, V> implements Serializable {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LruMemoryCache.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * a thread-safe double linked list is used to hold all objects.
     */
    private DoubleLinkedMap<K, byte[]> map;

    /**
     * Constructs a {@code LruMemoryCache} object.
     */
    public LruMemoryCache() {
        map = new DoubleLinkedMap<K, byte[]>();
    }

    @Override
    public void put(final K key, final V value) {
        putCountInc();

        synchronized (this) {
            if (getCachedCount() >= getMaxCount()) {
                collect();
            }

            try {
                map.addFirst(key, Serializer.serialize((Serializable) value));
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "Cache error[key={0}]", key);
                return;
            }

            cachedCountInc();
        }
    }

    /**
     * Just put sync, as the same as {@link #put(java.lang.Object, java.lang.Object)}.
     * 
     * No Async at present.
     * 
     * @param key the key of the specified object
     * @param value the specified object
     */
    @Override
    public void putAsync(final K key, final V value) {
        put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized V get(final K key) {
        final byte[] bytes = map.get(key);

        if (bytes != null) {
            hitCountInc();
            map.makeFirst(key);

            try {
                return (V) Serializer.deserialize(bytes);
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, "Gets cached object failed[key=" + key + "]", e);
                return null;
            }
        }

        missCountInc();
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void remove(final K key) {
        final boolean removed = map.remove(key);
        if (removed) {
            cachedCountDec();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void remove(final Collection<K> keys) {
        for (final K key : keys) {
            remove(key);
        }
    }

    /**
     * {@inheritDoc} Removes these useless objects directly. 
     */
    @Override
    public synchronized void collect() {
        map.removeLast();
        cachedCountDec();
    }

    @Override
    public synchronized void removeAll() {
        map.removeAll();
        setCachedCount(0);
        setMissCount(0);
        setHitCount(0);
    }

    @Override
    public boolean contains(final K key) {
        return null != get(key); // XXX: performance issue
    }

    @Override
    @SuppressWarnings("unchecked")
    public long inc(final K key, final long delta) {
        V ret = get(key);

        if (null == ret || !(ret instanceof Long)) {
            final Long v = delta;
            ret = (V) v;
            put(key, ret);
        }

        if (ret instanceof Long) {
            final Long v = (Long) ret + delta;
            ret = (V) v;
            put(key, ret);
        }

        return (Long) ret;
    }
}
