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
package org.b3log.latke.repository;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * None repository implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.2, Jun 6, 2019
 */
public final class NoneRepository implements Repository {

    /**
     * Constructs a none repository with the specified name.
     *
     * @param name the specified name
     */
    public NoneRepository(final String name) {
    }

    @Override
    public String add(final JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(final String id, final JSONObject jsonObject, final String... propertyNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(final String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(final Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject get(final String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean has(final String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JSONObject get(final Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JSONObject> select(final String statement, final Object... params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long count(final Query query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "None Repository";
    }

    @Override
    public Transaction beginTransaction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasTransactionBegun() {
        return false;
    }

    @Override
    public boolean isWritable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWritable(final boolean writable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDebug(final boolean debugEnabled) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
