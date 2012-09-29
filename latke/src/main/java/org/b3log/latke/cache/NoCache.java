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
package org.b3log.latke.cache;

import java.io.Serializable;
import java.util.Collection;

/**
 * Do not cache.
 * 
 * <p>
 * Has no effect by calling any caching related operations on this cache, it is a mock cache implementation.
 * </p>
 * 
 * @param <K> the key of an object
 * @param <V> the type of objects
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 19, 2012
 */
public final class NoCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

    /**
     * Name of this cache.
     */
    private String name;

    /**
     * Constructs with the specified name.
     *
     * @param name the specified name
     */
    public NoCache(final String name) {
        this.name = name;
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
        return false;
    }

    @Override
    public void put(final K key, final V value) {
    }

    @Override
    public void putAsync(final K key, final V value) {
    }

    @Override
    public V get(final K key) {
        return null;
    }

    @Override
    public long inc(final K key, final long delta) {
        return 0;
    }

    @Override
    public void remove(final K key) {
    }

    @Override
    public void remove(final Collection<K> keys) {
    }

    @Override
    public void removeAll() {
    }

    @Override
    public void setMaxCount(final long maxCount) {
    }

    @Override
    public long getMaxCount() {
        return 0;
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
        return 0;
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
    }
}
