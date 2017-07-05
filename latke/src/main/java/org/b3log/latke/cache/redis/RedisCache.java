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
package org.b3log.latke.cache.redis;

import org.b3log.latke.cache.AbstractCache;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Redis cache.
 *
 * @param <K> the type of the key of the object
 * @param <V> the type of the objects
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 5, 2017
 * @since 2.3.13
 */
public final class RedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RedisCache.class);

    @Override
    public boolean contains(final K key) {
        try (final Jedis jedis = Connections.getJedis()) {
            return jedis.exists(key.toString());
        }
    }

    @Override
    public void put(final K key, final V value) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.set(key.toString(), value.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Put data to cache with key [" + key.toString() + "] failed", e);
        }
    }

    @Override
    public V get(final K key) {
        try (final Jedis jedis = Connections.getJedis()) {
            final String s = jedis.get(key.toString());
            final JSONObject ret = new JSONObject(s);

            return (V) ret;
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Get data from cache with key [" + key.toString() + "] failed", e);

            return null;
        }
    }

    @Override
    public long inc(final K key, final long delta) {
        try (final Jedis jedis = Connections.getJedis()) {
            return jedis.incrBy(key.toString(), delta);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Inc data to cache with key [" + key.toString() + "] failed", e);

            return Long.MIN_VALUE;
        }
    }

    @Override
    public void remove(final K key) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.del(key.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with key [" + key.toString() + "] failed", e);
        }
    }

    @Override
    public void remove(final Collection<K> keys) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.del(keys.toArray(new String[]{}));
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with keys [" + keys.toString() + "] failed", e);
        }
    }

    @Override
    public void removeAll() {
        try (final Jedis jedis = Connections.getJedis()) {
            final Set<String> keys = jedis.keys("*");
            remove((Set<K>) keys);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Clear cache failed", e);
        }
    }

    @Override
    public void collect() {
    }
}
