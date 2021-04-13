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
 * @version 1.3.0.0, Apr 13, 2021
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

    /**
     * Constructor with the specified expire seconds.
     *
     * @param expireSeconds the specified expire seconds
     */
    public RedisCache(int expireSeconds) {
        super(expireSeconds);
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
        put(key, value, this.expireSeconds);
    }

    @Override
    public void put(final String key, final JSONObject value, final int expireSeconds) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.setex(getKeyPrefix() + key, expireSeconds, value.toString());
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

    @Override
    public int size() {
        try (final Jedis jedis = Connections.getJedis()) {
            final Set<String> keys = jedis.keys(getKeyPrefix() + "*");
            return keys.size();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Clear cache failed", e);
            return 0;
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
