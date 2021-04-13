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
package org.b3log.latke.cache;

import org.json.JSONObject;

import java.util.Collection;

/**
 * None cache.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Apr 13, 2021
 * @since 2.3.13
 */
public final class NoneCache extends AbstractCache {

    /**
     * Constructor with the specified expire seconds.
     *
     * @param expireSeconds the specified expire seconds
     */
    public NoneCache(int expireSeconds) {
        super(expireSeconds);
    }

    @Override
    public boolean contains(final String key) {
        return false;
    }

    @Override
    public void put(final String key, final JSONObject value) {
    }

    @Override
    public void put(final String key, final JSONObject value, final int expireSeconds) {
    }

    @Override
    public JSONObject get(final String key) {
        return null;
    }

    @Override
    public void remove(final String key) {
    }

    @Override
    public void remove(final Collection<String> keys) {
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }
}
