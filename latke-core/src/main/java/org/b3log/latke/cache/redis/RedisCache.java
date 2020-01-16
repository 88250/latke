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
package org.b3log.latke.cache.redis;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.AbstractCache;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Redis cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Dec 8, 2019
 * @since 2.3.13
 */
public final class RedisCache extends AbstractCache {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(RedisCache.class);

    /**
     * Key prefix.
     */
    private static final String KEY_PREFIX;

    static {
        String keyPrefix = Latkes.getLocalProperty("redis.keyPrefix");
        if (StringUtils.isBlank(keyPrefix)) {
            keyPrefix = "latke";
        }
        KEY_PREFIX = keyPrefix;
    }

    @Override
    public boolean contains(final String key) {
        try (final Jedis jedis = Connections.getJedis()) {
            return jedis.exists(getKeyPrefix() + key);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Contains key [" + key + "] failed", e);

            return false;
        }
    }

    @Override
    public void put(final String key, final JSONObject value) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.setex(getKeyPrefix() + key, EXPIRE_SECONDS, value.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Put data to cache with key [" + key + "] failed", e);
        }
    }

    @Override
    public JSONObject get(final String key) {
        try (final Jedis jedis = Connections.getJedis()) {
            final String s = jedis.get(getKeyPrefix() + key);
            if (null == s) {
                return null;
            }

            return new JSONObject(s);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get data from cache with key [" + key + "] failed", e);

            return null;
        }
    }

    @Override
    public void remove(final String key) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.del(getKeyPrefix() + key);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with key [" + key + "] failed", e);
        }
    }

    @Override
    public void remove(final Collection<String> keys) {
        final List<String> cacheKeys = new ArrayList<>(keys.size());
        final String keyPrefix = getKeyPrefix();
        for (final String key : keys) {
            cacheKeys.add(keyPrefix + key);
        }

        try (final Jedis jedis = Connections.getJedis()) {
            jedis.del(cacheKeys.toArray(new String[]{}));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with keys [" + keys + "] failed", e);
        }
    }

    @Override
    public void clear() {
        try (final Jedis jedis = Connections.getJedis()) {
            final Set<String> keys = jedis.keys(getKeyPrefix() + "*");
            if (keys.isEmpty()) {
                return;
            }

            jedis.del(keys.toArray(new String[]{}));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Clear cache failed", e);
        }
    }

    /**
     * Shutdowns redis cache.
     */
    public static void shutdown() {
        try {
            Connections.shutdown();
            LOGGER.debug("Closed Redis connection pool");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shutdown redis connection pool failed", e);
        }
    }

    private String getKeyPrefix() {
        return KEY_PREFIX + getName();
    }
}
