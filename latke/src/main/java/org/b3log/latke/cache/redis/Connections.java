package org.b3log.latke.cache.redis;

import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeCache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis connection utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 6, 2017
 * @since 2.3.13
 */
final class Connections {

    private static JedisPool pool;

    static {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        final RuntimeCache runtimeCache = Latkes.getRuntimeCache();
        if (RuntimeCache.REDIS == runtimeCache) {
            final int minConnCnt = Integer.valueOf(Latkes.getLocalProperty("redis.minConnCnt"));
            jedisPoolConfig.setMinIdle(minConnCnt);
            final int maxConnCnt = Integer.valueOf(Latkes.getLocalProperty("redis.maxConnCnt"));
            jedisPoolConfig.setMaxTotal(maxConnCnt);
            final String host = Latkes.getLocalProperty("redis.host");
            final int port = Integer.valueOf(Latkes.getLocalProperty("redis.port"));
            final String password = Latkes.getLatkeProperty("redis.password");

            pool = new JedisPool(jedisPoolConfig, host, port, 2000, password);
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
}
