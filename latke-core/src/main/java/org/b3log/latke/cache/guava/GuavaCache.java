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
package org.b3log.latke.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.b3log.latke.cache.AbstractCache;
import org.json.JSONObject;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Guava cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.2.0.0, Apr 13, 2021
 * @since 2.4.48
 */
public final class GuavaCache extends AbstractCache {

    private final Cache<String, JSONObject> cache;

    /**
     * Constructor with the specified expire seconds.
     *
     * @param expireSeconds the specified expire seconds
     */
    public GuavaCache(final int expireSeconds) {
        super(expireSeconds);
        cache = CacheBuilder.newBuilder().expireAfterWrite(expireSeconds, TimeUnit.SECONDS).build();
    }

    @Override
    public boolean contains(final String key) {
        return null != cache.getIfPresent(key);
    }

    @Override
    public void put(final String key, final JSONObject value) {
        cache.put(key, value);
    }

    @Override
    public void put(final String key, final JSONObject value, final int expireSeconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject get(final String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void remove(final String key) {
        cache.invalidate(key);
    }

    @Override
    public void remove(final Collection<String> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public int size() {
        return (int) cache.size();
    }
}
