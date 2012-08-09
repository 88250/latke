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
 * This is the top interface of cache like structures.
 * 
 * <p>
 * This cache can not hold {@code null} key or value.
 * </p>
 *
 * @param <K> the key of an object
 * @param <V> the type of objects
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.9, Dec 3, 2011
 */
public interface Cache<K extends Serializable, V extends Serializable> {

    /**
     * Checks whether an object specified by the given key is in cache.
     * 
     * @param key the given key
     * @return {@code true} if it is in cache, returns {@code false} otherwise
     */
    boolean contains(final K key);

    /**
     * Puts the specified object into this cache.
     *
     * <p>
     * Throws {@link IllegalArgumentException} if the specified key or value is 
     * {@code null}.
     * </p>
     *
     * @param key the key of the specified object
     * @param value the specified object
     */
    void put(final K key, final V value);

    /**
     * Puts the specified object into this cache asynchronously.
     * 
     * <p>
     * Throws {@link IllegalArgumentException} if the specified key or value is 
     * {@code null}.
     * </p>
     * 
     * @param key the key of the specified object
     * @param value the specified object
     */
    void putAsync(final K key, final V value);

    /**
     * Gets a object by the specified key.
     * 
     * <p>
     * Returns {@code null} if the specified key is {@code null}.
     * </p>
     * 
     * @param key the specified key
     * @return if found, returns the object, otherwise returns {@code null}
     */
    V get(final K key);

    /**
     * Increments the value specified by the given key with the specified delta.
     *
     * <p>
     * If the value specified by the given key is not present in this cache,
     * initialize it as {@code 1L}.
     * </p>
     * 
     * <p>
     * Throws {@link IllegalArgumentException} if the specified key is 
     * {@code null}.
     * </p>
     *
     * @param key the given key
     * @param delta the specified delta value
     * @return the post-increment value
     */
    long inc(final K key, final long delta);

    /**
     * Removes a object by the specified key.
     * 
     * @param key the specified key
     */
    void remove(final K key);

    /**
     * Removes objects by the specified keys.
     * 
     * @param keys the specified keys
     */
    void remove(final Collection<K> keys);

    /**
     * Removes all cached objects.
     */
    void removeAll();

    /**
     * Sets the maximum objects count of this cache.
     *
     * @param maxCount the maximum count of this cache
     */
    void setMaxCount(final long maxCount);

    /**
     * Gets the maximum objects count of this cache.
     *
     * @return the maximum objects count of this cache, returns {@code -1} if
     * cache is unavailable.
     */
    long getMaxCount();

    /**
     * Gets the hit count of this cache.
     *
     * @return hit count of this cache, returns {@code -1} if cache is unavailable.
     */
    long getHitCount();

    /**
     * Gets the miss count of this cache.
     *
     * @return miss count of this cache, returns {@code -1} if cache is unavailable.
     */
    long getMissCount();

    /**
     * Gets the put count of this cache.
     *
     * @return put count of this cache
     */
    long getPutCount();

    /**
     * Gets current cached object count of this cache.
     *
     * @return current cached object count of this cache, returns {@code -1}
     * if cache is unavailable.
     */
    long getCachedCount();

    /**
     * Gets cached bytes.
     *
     * @return cached bytes, returns {@code -1} if cache is unavailable.
     */
    long getCachedBytes();

    /**
     * Gets hit bytes.
     *
     * @return hit bytes, returns {@code -1} if cache is unavailable.
     */
    long getHitBytes();

    /**
     * Collects all useless cached objects. 
     */
    void collect();
}
