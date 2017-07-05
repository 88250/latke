package org.b3log.latke.cache.redis;

import org.b3log.latke.cache.AbstractCache;
import org.b3log.latke.logging.Logger;

import java.io.Serializable;
import java.util.Collection;

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
    public boolean contains(K key) {
        return false;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public long inc(K key, long delta) {
        return 0;
    }

    @Override
    public void remove(K key) {

    }

    @Override
    public void remove(Collection<K> keys) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void collect() {

    }
}
