/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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
package org.b3log.latke.repository.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Ids;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

/**
 * Redis repository implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Sep 4, 2016
 */
public class RedisRepository implements Repository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RedisRepository.class.getName());

    /**
     * Repository name.
     */
    private String name;

    /**
     * Writable?
     */
    private boolean writable = true;

    /**
     * Constructs a Redis repository with the specified name.
     *
     * @param name the specified name
     */
    public RedisRepository(final String name) {
        this.name = name;
    }

    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        final Jedis jedis = getJedis();

        final Map<String, String> map = new HashMap<String, String>();
        final JSONArray names = jsonObject.names();
        for (int i = 0; i < names.length(); i++) {
            final String n = names.optString(i);
            map.put(n, jsonObject.optString(n));
        }

        String ret = jsonObject.optString(Keys.OBJECT_ID);
        if (StringUtils.isBlank(ret)) {
            ret = Ids.genTimeMillisId();
            jsonObject.put(Keys.OBJECT_ID, ret);
        }

        jedis.hmset(ret, map);

        return ret;
    }

    @Override
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        final Jedis jedis = getJedis();

        final Map<String, String> map = new HashMap<String, String>();
        final JSONArray names = jsonObject.names();
        for (int i = 0; i < names.length(); i++) {
            final String n = names.optString(i);
            map.put(n, jsonObject.optString(n));
        }

        if (!jedis.exists(id)) {
            return;
        }

        jedis.hmset(id, map);
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        final Jedis jedis = getJedis();

        jedis.del(id);
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        final Jedis jedis = getJedis();

        final JSONObject ret = new JSONObject();

        final Map<String, String> map = jedis.hgetAll(id);
        if (null == map || 0 == map.size()) {
            return null;
        }

        for (final Map.Entry<String, String> entry : map.entrySet()) {
            final String k = entry.getKey();
            final String v = entry.getValue();

            final Object value = mapDataValue(k, v);

            ret.put(k, value);
        }

        return ret;
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        final Map<String, JSONObject> ret = new HashMap<String, JSONObject>();

        for (final String id : ids) {
            final JSONObject record = get(id);

            ret.put(id, record);
        }

        return ret;
    }

    @Override
    public boolean has(final String id) throws RepositoryException {
        final Jedis jedis = getJedis();

        return jedis.exists(id);
    }

    @Override
    public JSONObject get(final Query query) throws RepositoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JSONObject> select(final String statement, final Object... params) throws RepositoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long count() throws RepositoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long count(final Query query) throws RepositoryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Transaction beginTransaction() {
        return new RedisTransaction();
    }

    @Override
    public boolean hasTransactionBegun() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public void setWritable(final boolean writable) {
        this.writable = writable;
    }

    /**
     * Gets the Redis client.
     *
     * @return Redis client
     */
    public static Jedis getJedis() {
        final Jedis ret = new Jedis(Latkes.getLocalProperty("redis.URL"));
        final String ping = ret.ping();
        LOGGER.error(ping);

        final String password = Latkes.getLocalProperty("redis.password");
        if (null != password) {
            ret.auth(password);
        }

        return ret;
    }

    /**
     * Maps the specified data.
     *
     * @param fieldName field name of the specified data
     * @param fieldValue field value of the specified data
     * @return mapped value
     */
    private Object mapDataValue(final String fieldName, final String fieldValue) {
        final JSONArray keysDes = Repositories.getRepositoryKeysDescription(name);
        for (int i = 0; i < keysDes.length(); i++) {
            final JSONObject keyDes = keysDes.optJSONObject(i);
            final String keyName = keyDes.optString("name");

            if (!keyName.equals(fieldName)) {
                continue;
            }

            final String dataType = keyDes.optString("type");

            switch (dataType) {
                case "String":
                    return String.valueOf(fieldValue);
                case "int":
                    return Integer.valueOf(fieldValue);
                case "long":
                    return Long.valueOf(fieldValue);
                case "boolean":
                    return Boolean.valueOf(fieldValue);
                case "double":
                    return Double.valueOf(fieldValue);
                default:
                    LOGGER.warn("Unknown data type [" + dataType + "]");

                    return String.valueOf(fieldValue);
            }
        }

        return null;
    }
}
