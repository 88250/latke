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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache factory.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.1.1.6, Apr 28, 2020
 */
public final class CacheFactory {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(CacheFactory.class);

    /**
     * Caches.
     */
    private static final Map<String, Cache> CACHES = Collections.synchronizedMap(new HashMap<>());

    /**
     * Fixed expire seconds after put, 6 hours.
     */
    protected static final int EXPIRE_SECONDS = 60 * 60 * 6;

    /**
     * Private constructor.
     */
    private CacheFactory() {
    }

    /**
     * Gets a cache specified by the given cache name and expire seconds.
     *
     * @param cacheName     the given cache name
     * @param expireSeconds the given expire seconds
     * @return a cache instance
     */
    public static Cache getCache(final String cacheName, final int expireSeconds) {
        LOGGER.log(Level.INFO, "Constructing cache [name={}]....", cacheName);

        Cache ret = CACHES.get(cacheName);

        try {
            if (null == ret) {
                Class<Cache> cacheClass;
                switch (Latkes.getRuntimeCache()) {
                    case LOCAL_LRU:
                        cacheClass = (Class<Cache>) Class.forName("org.b3log.latke.cache.guava.GuavaCache");
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

                final Constructor<Cache> constructor = cacheClass.getConstructor(int.class);
                ret = constructor.newInstance(expireSeconds);
                ret.setName(cacheName);
                CACHES.put(cacheName, ret);
            }
        } catch (final Exception e) {
            throw new RuntimeException("Can not get cache: " + e.getMessage(), e);
        }

        LOGGER.log(Level.INFO, "Constructed cache [name={}, runtime={}]", cacheName, Latkes.getRuntimeCache());

        return ret;
    }

    /**
     * Gets a cache specified by the given cache name.
     *
     * @param cacheName the given cache name
     * @return a cache specified by the given cache name
     */
    public static Cache getCache(final String cacheName) {
        return getCache(cacheName, EXPIRE_SECONDS);
    }

    /**
     * Clears all caches.
     */
    public static synchronized void clear() {
        for (final Map.Entry<String, Cache> entry : CACHES.entrySet()) {
            final Cache cache = entry.getValue();
            cache.clear();
            LOGGER.log(Level.TRACE, "Cleared cache [name={}]", entry.getKey());
        }
    }
}
