/*
 * Copyright (c) 2009-2019, b3log.org & hacpai.com
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
package org.b3log.latke.cache.memory;

import org.b3log.latke.cache.AbstractCache;
import org.b3log.latke.logging.Logger;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Collection;

/**
 * This is a Least Recently Used (LRU) pure memory cache. This cache use a thread-safe {@link DoubleLinkedMap} to hold
 * the objects, and the least recently used objects will be moved to the end of the list and to remove by invoking
 * {@link #collect()} method.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.3.11, Oct 27, 2018
 */
public final class LruMemoryCache extends AbstractCache implements Serializable {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LruMemoryCache.class);

    /**
     * a thread-safe double linked list is used to hold all objects.
     */
    private DoubleLinkedMap<String, JSONObject> map;

    /**
     * Constructs a {@code LruMemoryCache} object.
     */
    public LruMemoryCache() {
        map = new DoubleLinkedMap<>();
    }

    @Override
    public void put(final String key, final JSONObject value) {
        remove(key);

        putCountInc();

        synchronized (this) {
            if (getCachedCount() >= getMaxCount()) {
                collect();
            }

            map.addFirst(key, value);

            cachedCountInc();
        }
    }

    @Override
    public synchronized JSONObject get(final String key) {
        final JSONObject ret = map.get(key);
        if (null != ret) {
            hitCountInc();
            map.makeFirst(key);

            return ret;
        }

        missCountInc();

        return null;
    }

    @Override
    public synchronized void remove(final String key) {
        final boolean removed = map.remove(key);
        if (removed) {
            cachedCountDec();
        }
    }

    @Override
    public synchronized void remove(final Collection<String> keys) {
        for (final String key : keys) {
            remove(key);
        }
    }

    @Override
    public synchronized void collect() {
        map.removeLast();
        cachedCountDec();
    }

    @Override
    public synchronized void clear() {
        map.removeAll();
        setCachedCount(0);
        setMissCount(0);
        setHitCount(0);
    }

    @Override
    public boolean contains(final String key) {
        return null != get(key); // XXX: performance issue
    }
}
