/*
 * Copyright (c) 2009-present, b3log.org
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

import org.json.JSONObject;

import java.util.Collection;

/**
 * This is the top interface of cache like structures.
 * <p>
 * This cache can not hold {@code null} key or value.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.1.0.0, Jun 8, 2019
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
     * <p>
     * Throws {@link IllegalArgumentException} if the specified key or value is {@code null}.
     * </p>
     *
     * @param key   the key of the specified object
     * @param value the specified object
     */
    void put(final String key, final JSONObject value);

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
}
