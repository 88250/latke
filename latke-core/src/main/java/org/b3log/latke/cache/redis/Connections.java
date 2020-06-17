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
import org.b3log.latke.util.CollectionUtils;
import redis.clients.jedis.*;

import java.util.Set;

/**
 * Redis connection utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Dec 9, 2019
 * @since 2.3.13
 */
public final class Connections {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Connections.class);

    /**
     * Pool.
     */
    private static JedisPoolAbstract pool;

    static {
        try {
            final Latkes.RuntimeCache runtimeCache = Latkes.getRuntimeCache();
            if (Latkes.RuntimeCache.REDIS == runtimeCache) {
                final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                final int minConnCnt = Integer.parseInt(Latkes.getLocalProperty("redis.minConnCnt"));
                jedisPoolConfig.setMinIdle(minConnCnt);
                final int maxConnCnt = Integer.parseInt(Latkes.getLocalProperty("redis.maxConnCnt"));
                jedisPoolConfig.setMaxTotal(maxConnCnt);
                String password = Latkes.getLocalProperty("redis.password");
                if (StringUtils.isBlank(password)) {
                    password = null;
                }
                final long waitTime = Long.parseLong(Latkes.getLocalProperty("redis.waitTime"));
                jedisPoolConfig.setMaxWaitMillis(waitTime);

                final String masterName = Latkes.getLocalProperty("redis.master");
                if (StringUtils.isNotBlank(masterName)) {
                    final String[] sentinelArray = Latkes.getLocalProperty("redis.sentinels").split(",");
                    final Set<String> sentinels = CollectionUtils.arrayToSet(sentinelArray);
                    pool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig, password);
                } else {
                    final String host = Latkes.getLocalProperty("redis.host");
                    final int port = Integer.parseInt(Latkes.getLocalProperty("redis.port"));
                    pool = new JedisPool(jedisPoolConfig, host, port, Protocol.DEFAULT_TIMEOUT, password);
                }
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
    public static Jedis getJedis() {
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
