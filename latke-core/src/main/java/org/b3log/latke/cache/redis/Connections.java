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
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Redis connection utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.0, Oct 25, 2018
 * @since 2.3.13
 */
final class Connections {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Connections.class);

    /**
     * Pool.
     */
    private static JedisPool pool;

    static {
        try {
            final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

            final Latkes.RuntimeCache runtimeCache = Latkes.getRuntimeCache();
            if (Latkes.RuntimeCache.REDIS == runtimeCache) {
                final int minConnCnt = Integer.valueOf(Latkes.getLocalProperty("redis.minConnCnt"));
                jedisPoolConfig.setMinIdle(minConnCnt);
                final int maxConnCnt = Integer.valueOf(Latkes.getLocalProperty("redis.maxConnCnt"));
                jedisPoolConfig.setMaxTotal(maxConnCnt);
                final String host = Latkes.getLocalProperty("redis.host");
                final int port = Integer.valueOf(Latkes.getLocalProperty("redis.port"));
                String password = Latkes.getLocalProperty("redis.password");
                if (StringUtils.isBlank(password)) {
                    password = null;
                }
                final long waitTime = Long.valueOf(Latkes.getLocalProperty("redis.waitTime"));
                jedisPoolConfig.setMaxWaitMillis(waitTime);

                pool = new JedisPool(jedisPoolConfig, host, port, Protocol.DEFAULT_TIMEOUT, password);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Initializes redis connection pool failed", e);
        }
    }

    /**
     * Gets a jedis.
     *
     * @return jedis
     */
    static Jedis getJedis() {
        return pool.getResource();
    }

    /**
     * Shutdowns pool.
     */
    static void shutdown() {
        pool.close();
    }

    /**
     * Private constructor.
     */
    private Connections() {
    }
}
