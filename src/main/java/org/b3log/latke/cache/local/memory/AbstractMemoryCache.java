/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.cache.local.memory;

import java.io.Serializable;
import org.b3log.latke.cache.Cache;

/**
 * The abstract memory cache. 
 *
 * @param <K> the type of the key of objects
 * @param <V> the type of objects
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.6, Dec 3, 2011
 */
public abstract class AbstractMemoryCache<K extends Serializable, V extends Serializable>
        implements Cache<K, V> {

    /**
     * Maximum objects count of this cache.
     */
    private long maxCount = Long.MAX_VALUE;
    /**
     * Hit count of this cache.
     */
    private long hitCount;
    /**
     * Miss count of this cache.
     */
    private long missCount;
    /**
     * Put count of this cache.
     */
    private long putCount;
    /**
     * Cached object count of this cache.
     */
    private long cachedCount;

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getHitCount() {
        return hitCount;
    }

    /**
     * Sets his count by the specified hit count.
     * 
     * @param hitCount the specified hit count
     */
    public final void setHitCount(final int hitCount) {
        this.hitCount = hitCount;
    }

    /**
     * Adds one to hit count itself.
     */
    protected final void hitCountInc() {
        hitCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getMissCount() {
        return missCount;
    }

    /**
     * Sets miss count by the specified miss count.
     *
     * @param missCount the specified miss count
     */
    public final void setMissCount(final long missCount) {
        this.missCount = missCount;
    }

    /**
     * Adds one to miss count itself.
     */
    protected final void missCountInc() {
        missCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getPutCount() {
        return putCount;
    }

    /**
     * Sets put count by the specified put count.
     *
     * @param putCount the specified put count
     */
    protected final void setPutCount(final long putCount) {
        this.putCount = putCount;
    }

    /**
     * Adds one to put count itself.
     */
    protected final void putCountInc() {
        putCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getCachedCount() {
        return cachedCount;
    }

    /**
     * Adds one to cached count itself.
     */
    protected final void cachedCountInc() {
        cachedCount++;
    }

    /**
     * Subtracts one to cached count itself.
     */
    protected final void cachedCountDec() {
        cachedCount--;
    }

    /**
     * Sets the cached count with the specified cached count.
     *
     * @param cachedCount the specified cache count
     */
    protected final void setCachedCount(final long cachedCount) {
        this.cachedCount = cachedCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getMaxCount() {
        return maxCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setMaxCount(final long maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public long getCachedBytes() {
        // TODO: getCachedBytes
        return -1;
    }

    @Override
    public long getHitBytes() {
        // TODO: getHitBytes
        return -1;
    }
}
