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

import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.1.1.4, Mar 26, 2019
 */
public final class CacheFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CacheFactory.class);

    /**
     * Caches.
     */
    private static final Map<String, Cache> CACHES = Collections.synchronizedMap(new HashMap<>());

    /**
     * Private constructor.
     */
    private CacheFactory() {
    }

    /**
     * Gets a cache specified by the given cache name.
     *
     * @param cacheName the given cache name
     * @return a cache specified by the given cache name
     */
    public static synchronized Cache getCache(final String cacheName) {
        LOGGER.log(Level.INFO, "Constructing cache [name={0}]....", cacheName);

        Cache ret = CACHES.get(cacheName);

        try {
            if (null == ret) {
                Class<Cache> cacheClass;
                switch (Latkes.getRuntimeCache()) {
                    case LOCAL_LRU:
                        cacheClass = (Class<Cache>) Class.forName("org.b3log.latke.cache.caffeine.CaffeineCache");

                        break;
                    case REDIS:
                        cacheClass = (Class<Cache>) Class.forName("org.b3log.latke.cache.redis.RedisCache");

                        break;
                    case NONE:
                        cacheClass = (Class<Cache>) Class.forName("org.b3log.latke.cache.NoneCache");

                        break;
                    default:
                        throw new RuntimeException("Latke runs in the hell.... Please set the environment correctly");
                }

                ret = cacheClass.newInstance();

                ret.setName(cacheName);
                CACHES.put(cacheName, ret);
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not get cache: " + e.getMessage(), e);
        }

        LOGGER.log(Level.INFO, "Constructed cache [name={0}, runtime={1}]", cacheName, Latkes.getRuntimeCache());

        return ret;
    }

    /**
     * Clears all caches.
     */
    public static synchronized void clear() {
        for (final Map.Entry<String, Cache> entry : CACHES.entrySet()) {
            final Cache cache = entry.getValue();
            cache.clear();
            LOGGER.log(Level.TRACE, "Cleared cache [name={0}]", entry.getKey());
        }
    }
}
