/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
 * Do not cache.
 * <p>
 * Has no effect by calling any caching related operations on this cache, it is a mock cache implementation.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Jul 6, 2017
 */
public final class NoCache implements Cache {

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
    public boolean contains(final String key) {
        return false;
    }

    @Override
    public void put(final String key, final JSONObject value) {
    }

    @Override
    public void putAsync(final String key, final JSONObject value) {
    }

    @Override
    public JSONObject get(final String key) {
        return null;
    }

    @Override
    public void remove(final String key) {
    }

    @Override
    public void remove(final Collection<String> keys) {
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
    public long getCachedCount() {
        return -1;
    }

    @Override
    public void collect() {
    }
}
