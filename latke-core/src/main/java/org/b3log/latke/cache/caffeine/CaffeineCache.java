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
package org.b3log.latke.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.b3log.latke.cache.AbstractCache;
import org.json.JSONObject;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 26, 2019
 * @since 2.4.48
 */
public final class CaffeineCache extends AbstractCache {

    private Cache<String, JSONObject> cache = Caffeine.newBuilder().expireAfterWrite(EXPIRE_SECONDS, TimeUnit.SECONDS).build();

    @Override
    public boolean contains(final String key) {
        return null != cache.getIfPresent(key);
    }

    @Override
    public void put(final String key, final JSONObject value) {
        cache.put(key, value);
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
}
