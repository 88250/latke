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

import java.util.Collection;
import java.util.Set;

/**
 * Redis cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 6, 2017
 * @since 2.3.13
 */
public final class RedisCache extends AbstractCache {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RedisCache.class);

    @Override
    public boolean contains(final String key) {
        try (final Jedis jedis = Connections.getJedis()) {
            return jedis.exists(key);
        }
    }

    @Override
    public void put(final String key, final JSONObject value) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.set(key, value.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Put data to cache with key [" + key.toString() + "] failed", e);
        }
    }

    @Override
    public JSONObject get(final String key) {
        try (final Jedis jedis = Connections.getJedis()) {
            return new JSONObject(jedis.get(key.toString()));
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Get data from cache with key [" + key.toString() + "] failed", e);

            return null;
        }
    }

    @Override
    public void remove(final String key) {
        try (final Jedis jedis = Connections.getJedis()) {
            jedis.del(key.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove data to cache with key [" + key.toString() + "] failed", e);
        }
    }

    @Override
    public void remove(final Collection<String> keys) {
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
            remove((Set<String>) keys);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Clear cache failed", e);
        }
    }

    @Override
    public void collect() {
    }
}
