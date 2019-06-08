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

import org.b3log.latke.cache.AbstractCache;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
 * @version 1.0.1.1, Mar 26, 2019
 * @since 2.3.13
 */
public final class RedisCache extends AbstractCache {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RedisCache.class);

    @Override
    public boolean contains(final String key) {
        Jedis jedis = null;
        try {
            jedis = Connections.getJedis();

            return jedis.exists(key);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Contains key [" + key + "] failed", e);

            return false;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public void put(final String key, final JSONObject value) {
        Jedis jedis = null;
        try {
            jedis = Connections.getJedis();

            jedis.setex(getName() + key, EXPIRE_SECONDS, value.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Put data to cache with key [" + key + "] failed", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public JSONObject get(final String key) {
        Jedis jedis = null;
        try {
            jedis = Connections.getJedis();

            final String s = jedis.get(getName() + key);
            if (null == s) {
                return null;
            }

            return new JSONObject(s);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get data from cache with key [" + key + "] failed", e);

            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public void remove(final String key) {
        Jedis jedis = null;
        try {
            jedis = Connections.getJedis();

            jedis.del(getName() + key);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with key [" + key + "] failed", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public void remove(final Collection<String> keys) {
        final List<String> cacheKeys = new ArrayList<>(keys.size());
        final String cacheName = getName();
        for (final String key : keys) {
            cacheKeys.add(cacheName + key);
        }

        Jedis jedis = null;
        try {
            jedis = Connections.getJedis();

            jedis.del(cacheKeys.toArray(new String[]{}));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with keys [" + keys + "] failed", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Override
    public void clear() {
        Jedis jedis = null;
        try {
            jedis = Connections.getJedis();

            final Set<String> keys = jedis.keys(getName() + "*");
            if (keys.isEmpty()) {
                return;
            }

            jedis.del(keys.toArray(new String[]{}));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Clear cache failed", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    /**
     * Shutdowns redis cache.
     */
    public static void shutdown() {
        try {
            Connections.shutdown();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shutdown redis connection pool failed", e);
        }
    }
}
