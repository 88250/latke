/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.cache;

import org.json.JSONObject;

import java.util.Collection;

/**
 * This is the top interface of cache like structures.
 * <p>
 * This cache can not hold {@code null} key or value.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.3.0.0, Apr 13, 2021
 */
public interface Cache {

    /**
     * Sets the name of this cache with the specified name.
     *
     * @param name the specified name
     */
    void setName(final String name);

    /**
     * Gets the name of this cache.
     *
     * @return name of this cache
     */
    String getName();

    /**
     * Checks whether an object specified by the given key is in cache.
     *
     * @param key the given key
     * @return {@code true} if it is in cache, returns {@code false} otherwise
     */
    boolean contains(final String key);

    /**
     * Puts the specified object into this cache.
     *
     * @param key   the key of the specified object
     * @param value the specified object
     */
    void put(final String key, final JSONObject value);

    /**
     * Puts the specified object into this cache with expire time.
     *
     * @param key           the key of the specified object
     * @param value         the specified object
     * @param expireSeconds the specified expire seconds
     */
    void put(final String key, final JSONObject value, final int expireSeconds);

    /**
     * Gets a object by the specified key.
     * <p>
     * Returns {@code null} if the specified key is {@code null}.
     * </p>
     *
     * @param key the specified key
     * @return if found, returns the object, otherwise returns {@code null}
     */
    JSONObject get(final String key);

    /**
     * Removes a object by the specified key.
     *
     * @param key the specified key
     */
    void remove(final String key);

    /**
     * Remove objects by the specified keys.
     *
     * @param keys the specified keys
     */
    void remove(final Collection<String> keys);

    /**
     * Clear all cached objects.
     */
    void clear();

    /**
     * Gets count of objects.
     */
    int size();
}
