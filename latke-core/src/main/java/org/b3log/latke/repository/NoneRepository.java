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
package org.b3log.latke.repository;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * None repository implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.1, Nov 4, 2018
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
    public void update(final String id, final JSONObject jsonObject) {
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
