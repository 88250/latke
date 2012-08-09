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
package org.b3log.latke.repository.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.CompositeFilter;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Projection;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.JdbcUtil;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JDBC repository implementation.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jul 17, 2012
 */
@SuppressWarnings("unchecked")
public final class JdbcRepository implements Repository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcRepository.class.getName());
    /**
     * Repository name.
     */
    private String name;
    /**
     * Is cache enabled?
     */
    private boolean cacheEnabled = true;
    /**
     * Writable?
     */
    private boolean writable = true;
    /**
     * Cache key prefix.
     */
    public static final String CACHE_KEY_PREFIX = "repository";
    /**
     * Repository cache count.
     */
    private static final String REPOSITORY_CACHE_COUNT = "#count";
    /**
     * Repository cache.
     * <p>
     * &lt;oId, JSONObject&gt;
     * </p>
     */
    public static final Cache<String, Serializable> CACHE;
    /**
     * Repository cache name.
     */
    public static final String REPOSITORY_CACHE_NAME = "repositoryCache";
    /**
     * The current transaction.
     */
    public static final ThreadLocal<JdbcTransaction> TX = new InheritableThreadLocal<JdbcTransaction>();

    static {
        CACHE = (Cache<String, Serializable>) CacheFactory.getCache(REPOSITORY_CACHE_NAME);
    }

    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        final JdbcTransaction currentTransaction = TX.get();
        if (null == currentTransaction) {
            throw new RepositoryException("Invoking add() outside a transaction");
        }

        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<Object>();
        final StringBuilder sql = new StringBuilder();
        String id = null;

        try {
            id = buildAddSql(jsonObject, paramList, sql);
            JdbcUtil.executeSql(sql.toString(), paramList, connection);
        } catch (final SQLException se) {
            LOGGER.log(Level.SEVERE, "add:"
                                     + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "add:"
                                     + e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return id;
    }

    /**
     * buildAddSql.
     * @param jsonObject jsonObject
     * @param paramlist paramlist 
     * @param sql sql
     * @return id
     * @throws Exception  exception
     */
    private String buildAddSql(final JSONObject jsonObject, final List<Object> paramlist, final StringBuilder sql)
            throws Exception {
        String ret;

        if (!jsonObject.has(Keys.OBJECT_ID)) {
            ret = Ids.genTimeMillisId();
            jsonObject.put(Keys.OBJECT_ID, ret);
        } else {
            ret = jsonObject.getString(Keys.OBJECT_ID);
        }

        setProperties(jsonObject, paramlist, sql);

        return ret;
    }

    /**
     * setProperties.
     * 
     * @param jsonObject jsonObject
     * @param paramlist paramlist
     * @param sql sql
     * @throws Exception exception 
     */
    private void setProperties(final JSONObject jsonObject, final List<Object> paramlist, final StringBuilder sql) throws Exception {
        final Iterator<String> keys = jsonObject.keys();

        final StringBuilder insertString = new StringBuilder();
        final StringBuilder wildcardString = new StringBuilder();

        boolean isFirst = true;
        String key;
        Object value;

        while (keys.hasNext()) {
            key = keys.next();

            if (isFirst) {
                insertString.append("(").append(key);
                wildcardString.append("(?");
                isFirst = false;
            } else {
                insertString.append(",").append(key);
                wildcardString.append(",?");
            }

            value = jsonObject.get(key);
            paramlist.add(value);

            if (!keys.hasNext()) {
                insertString.append(")");
                wildcardString.append(")");
            }
        }

        /*
         * TODO: Y, table name Prefix.
         */
        sql.append("insert into ").append(getName()).append(insertString).append(" value ").append(wildcardString);
    }

    @Override
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        if (Strings.isEmptyOrNull(id)) {
            return;
        }

        final JdbcTransaction currentTransaction = TX.get();

        if (null == currentTransaction) {
            throw new RepositoryException("Invoking update() outside a transaction");
        }

        final JSONObject oldJsonObject = get(id);

        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<Object>();
        final StringBuilder sqlBuilder = new StringBuilder();
        try {
            update(id, oldJsonObject, jsonObject, paramList, sqlBuilder);

            final String sql = sqlBuilder.toString();
            if (Strings.isEmptyOrNull(sql)) {
                return;
            }

            JdbcUtil.executeSql(sql, paramList, connection);
        } catch (final SQLException se) {
            LOGGER.log(Level.SEVERE, "update:"
                                     + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "update:"
                                     + e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    /**
     * 
     * update.
     * 
     * @param id id
     * @param oldJsonObject oldJsonObject
     * @param jsonObject newJsonObject
     * @param paramList paramList
     * @param sql sql
     * @throws JSONException JSONException
     */
    private void update(final String id, final JSONObject oldJsonObject,
                        final JSONObject jsonObject, final List<Object> paramList,
                        final StringBuilder sql) throws JSONException {
        final JSONObject needUpdateJsonObject = getNeedUpdateJsonObject(oldJsonObject, jsonObject);

        if (needUpdateJsonObject.length() == 0) {
            LOGGER.log(Level.INFO, "nothing to update [{0}] for repository[{1}]", new Object[]{id, getName()});
            return;
        }

        setUpdateProperties(id, needUpdateJsonObject, paramList, sql);
    }

    /**
     * setUpdateProperties.
     * 
     * @param id id
     * @param needUpdateJsonObject needUpdateJsonObject
     * @param paramList paramList
     * @param sql sql
     * @throws JSONException JSONException
     */
    private void setUpdateProperties(final String id, final JSONObject needUpdateJsonObject,
                                     final List<Object> paramList, final StringBuilder sql) throws JSONException {
        final Iterator<String> keys = needUpdateJsonObject.keys();
        String key;

        boolean isFirst = true;
        final StringBuilder wildcardString = new StringBuilder();

        while (keys.hasNext()) {
            key = keys.next();

            if (isFirst) {
                wildcardString.append(" set ").append(key).append("=?");
                isFirst = false;
            } else {
                wildcardString.append(",").append(key).append("=?");
            }

            paramList.add(needUpdateJsonObject.get(key));
        }

        sql.append("update ").append(getName()).append(wildcardString).append(" where ").
                append(JdbcRepositories.OID).append("=").append("?");
        paramList.add(id);
    }

    /**
     * 
     * getNeedUpdateJsonObject.
     * 
     * @param oldJsonObject oldJsonObject
     * @param jsonObject newJsonObject
     * @return JSONObject
     * @throws JSONException jsonObject
     */
    private JSONObject getNeedUpdateJsonObject(final JSONObject oldJsonObject, final JSONObject jsonObject) throws JSONException {
        final JSONObject needUpdateJsonObject = new JSONObject();

        final Iterator<String> keys = jsonObject.keys();

        String key;
        while (keys.hasNext()) {
            key = keys.next();

            if (jsonObject.get(key) == null
                && oldJsonObject.get(key) == null) {
                // ???????????????????????????
                needUpdateJsonObject.put(key, jsonObject.get(key));
            } else if (!jsonObject.getString(key).equals(
                    oldJsonObject.getString(key))) {
                needUpdateJsonObject.put(key, jsonObject.get(key));
            }
        }

        return needUpdateJsonObject;
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        if (Strings.isEmptyOrNull(id)) {
            return;
        }

        final JdbcTransaction currentTransaction = TX.get();

        if (null == currentTransaction) {
            throw new RepositoryException("Invoking remove() outside a transaction");
        }

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();

        try {
            remove(id, sql);
            JdbcUtil.executeSql(sql.toString(), connection);
        } catch (final SQLException se) {
            LOGGER.log(Level.SEVERE, "update:"
                                     + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "remove:"
                                     + e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    /**
     * remove record.
     * 
     * @param id id
     * @param sql sql
     */
    private void remove(final String id, final StringBuilder sql) {
        sql.append("delete from ").append(getName()).append(" where ").append(JdbcRepositories.OID).append("='").
                append(id).append("'");
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = null;

        if (cacheEnabled) {
            final String cacheKey = CACHE_KEY_PREFIX
                                    + id;
            ret = (JSONObject) CACHE.get(cacheKey);
            if (null != ret) {
                LOGGER.log(Level.FINER, "Got an object[cacheKey={0}] from repository cache[name={1}]", new Object[]{cacheKey, getName()});
                return ret;
            }
        }

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();

        try {
            get(sql);
            final ArrayList<Object> paramList = new ArrayList<Object>();
            paramList.add(id);
            ret = JdbcUtil.queryJsonObject(sql.toString(), paramList, connection, getName());

            if (cacheEnabled) {
                final String cacheKey = CACHE_KEY_PREFIX
                                        + id;
                CACHE.putAsync(cacheKey, ret);
                LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}]", new Object[]{cacheKey, getName()});
            }

        } catch (final SQLException e) {
            throw new JDBCRepositoryException(e);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "get:"
                                     + e.getMessage(), e);
            throw new RepositoryException(e);
        } finally {
            closeQueryConnection(connection);
        }

        return ret;
    }

    /**
     * get.
     * 
     * @param sql sql
     */
    private void get(final StringBuilder sql) {
        sql.append("select * from ").append(getName()).append(" where ").append(JdbcRepositories.OID).append("=").append("?");
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        final Map<String, JSONObject> map = new HashMap<String, JSONObject>();
        JSONObject jsonObject;

        for (final String id : ids) {
            jsonObject = get(id);
            map.put(jsonObject.optString(JdbcRepositories.OID), jsonObject);
        }

        return map;
    }

    @Override
    public boolean has(final String id) throws RepositoryException {
        // final StringBuilder sql = new StringBuilder("select count("
        // + JdbcRepositories.OID +
        // ") from ").append(getName()).append(" where ").append(
        // JdbcRepositories.OID).append("=").append(id);
        //
        // return count(sql, new ArrayList<Object>()) > 0;

        // using get() method to get result.

        return null != get(id);
    }

    @Override
    public JSONObject get(final Query query) throws RepositoryException {
        JSONObject ret = new JSONObject();

        final String cacheKey = CACHE_KEY_PREFIX
                                + query.getCacheKey() + "_" + getName();
        if (cacheEnabled) {
            ret = (JSONObject) CACHE.get(cacheKey);
            if (null != ret) {
                LOGGER.log(Level.FINER, "Got query result[cacheKey={0}] from repository cache[name={1}]",
                           new Object[]{cacheKey, getName()});
                return ret;
            }

            ret = new JSONObject(); // Re-instantiates it if cache miss
        }

        final int currentPageNum = query.getCurrentPageNum();
        final int pageSize = query.getPageSize();
        // final Map<String, SortDirection> sorts = query.getSorts();
        // final Set<Projection> projections = query.getProjections();
        // Asssumes the application call need to ccount page
        int pageCount = -1;
        // If the application caller need not to count page, gets the page count
        // the caller specified
        if (null != query.getPageCount()) {
            pageCount = query.getPageCount();
        }

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<Object>();

        try {
            final int pageCnt = get(currentPageNum, pageSize, pageCount, query, sql, paramList);

            // page
            final JSONObject pagination = new JSONObject();
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
            ret.put(Pagination.PAGINATION, pagination);

            // result
            if (pageCnt == 0) {
                ret.put(Keys.RESULTS, new JSONArray());
                return ret;
            }

            final JSONArray jsonResults = JdbcUtil.queryJsonArray(sql.toString(), paramList, connection, getName());
            ret.put(Keys.RESULTS, jsonResults);

            if (cacheEnabled) {
                CACHE.putAsync(cacheKey, ret);
                LOGGER.log(Level.FINER, "Added query result[cacheKey={0}] in repository cache[{1}]",
                           new Object[]{cacheKey, getName()});
                try {
                    cacheQueryResults(ret.optJSONArray(Keys.RESULTS), query);
                } catch (final JSONException e) {
                    LOGGER.log(Level.WARNING, "Caches query results failed", e);
                }
            }

        } catch (final SQLException e) {
            throw new JDBCRepositoryException(e);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "query: "
                                     + e.getMessage(), e);
            throw new RepositoryException(e);
        } finally {
            closeQueryConnection(connection);
        }

        return ret;
    }

    /**
     * Caches the specified query results with the specified query.
     * 
     * @param results the specified query results
     * @param query the specified query
     * @throws JSONException json exception
     */
    private void cacheQueryResults(final JSONArray results, final org.b3log.latke.repository.Query query)
            throws JSONException {
        String cacheKey;
        for (int i = 0; i < results.length(); i++) {
            final JSONObject jsonObject = results.optJSONObject(i);

            // 1. Caching for get by id.
            cacheKey = CACHE_KEY_PREFIX + jsonObject.optString(Keys.OBJECT_ID);
            CACHE.putAsync(cacheKey, jsonObject);
            LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}] for default index[oId]",
                       new Object[]{cacheKey, getName()});

            // 2. Caching for get by query with filters (EQUAL operator) only
            final Set<String[]> indexes = query.getIndexes();
            final StringBuilder logMsgBuilder = new StringBuilder();
            for (final String[] index : indexes) {
                final org.b3log.latke.repository.Query futureQuery = new org.b3log.latke.repository.Query().setPageCount(1);
                for (int j = 0; j < index.length; j++) {
                    final String propertyName = index[j];

                    futureQuery.setFilter(new PropertyFilter(propertyName, FilterOperator.EQUAL, jsonObject.opt(propertyName)));
                    logMsgBuilder.append(propertyName).append(",");
                }

                if (logMsgBuilder.length() > 0) {
                    logMsgBuilder.deleteCharAt(logMsgBuilder.length() - 1);
                }

                cacheKey = CACHE_KEY_PREFIX + futureQuery.getCacheKey() + "_" + getName();

                final JSONObject futureQueryRet = new JSONObject();
                final JSONObject pagination = new JSONObject();
                futureQueryRet.put(Pagination.PAGINATION, pagination);
                pagination.put(Pagination.PAGINATION_PAGE_COUNT, 1);

                final JSONArray futureQueryResults = new JSONArray();
                futureQueryRet.put(Keys.RESULTS, futureQueryResults);
                futureQueryResults.put(jsonObject);

                CACHE.putAsync(cacheKey, futureQueryRet);
                LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}] for index[{2}] for future query[{3}]",
                           new Object[]{cacheKey, getName(), logMsgBuilder, futureQuery.toString()});
            }
        }
    }

    /**
     * 
     * getQuery sql.
     * 
     * @param currentPageNum currentPageNum
     * @param pageSize pageSize
     * @param pageCount pageCount
     * @param query query
     * @param sql sql
     * @param paramList paramList
     * @return pageCnt
     * @throws RepositoryException  RepositoryException
     */
    private int get(final int currentPageNum, final int pageSize, final int pageCount, final Query query, final StringBuilder sql,
                    final List<Object> paramList)
            throws RepositoryException {
        int ret = pageCount;

        final StringBuilder selectSql = new StringBuilder();
        final StringBuilder filterSql = new StringBuilder();
        final StringBuilder orderBySql = new StringBuilder();
        getSelectSql(selectSql, query.getProjections());
        getFilterSql(filterSql, paramList, query.getFilter());
        getOrderBySql(orderBySql, query.getSorts());

        if (-1 == pageCount) {
            final StringBuilder countSql = new StringBuilder("select count(" + JdbcRepositories.OID + ") from ").append(getName());

            if (StringUtils.isNotBlank(filterSql.toString())) {
                countSql.append(" where ").append(filterSql);
            }

            final long count = count(countSql, paramList);
            ret = (int) Math.ceil((double) count / (double) pageSize);

            if (ret == 0) {
                return 0;
            }
        }

        if (currentPageNum > ret) {
            LOGGER.log(Level.WARNING, "Current page num[{0}] > page count[{1}]", new Object[]{currentPageNum, ret});
        }

        getQuerySql(currentPageNum, pageSize, selectSql, filterSql, orderBySql, sql);

        return ret;
    }

    /**
     * get select sql.
     * if projections size = 0 ,return select count(*).
     * @param selectSql selectSql 
     * @param projections projections
     */
    private void getSelectSql(final StringBuilder selectSql, final Set<Projection> projections) {
        if (projections == null || projections.isEmpty()) {
            selectSql.append(" select * ");
            return;
        }

        boolean isFisrt = true;
        selectSql.append(" select ");
        for (Projection projection : projections) {
            if (isFisrt) {
                selectSql.append(projection.getKey());
                isFisrt = false;
            } else {
                selectSql.append(",").append(projection.getKey());
            }
        }
    }

    /**
     * getQuerySql.
     * 
     * @param currentPageNum currentPageNum
     * @param pageSize  pageSize
     * @param selectSql selectSql
     * @param filterSql filterSql
     * @param orderBySql orderBySql
     * @param sql sql
     */
    private void getQuerySql(final int currentPageNum, final int pageSize, final StringBuilder selectSql,
                             final StringBuilder filterSql, final StringBuilder orderBySql, final StringBuilder sql) {
        final int start = (currentPageNum - 1) * pageSize;
        final int end = start + pageSize;

        sql.append(JdbcFactory.createJdbcFactory().queryPage(start, end, selectSql.toString(), filterSql.toString(), orderBySql.toString(),
                                                             getName()));
    }

    /**
     * 
     * get filterSql and paramList.
     * 
     * @param filterSql filterSql
     * @param paramList paramList
     * @param filter filter
     * @throws RepositoryException RepositoryException
     */
    private void getFilterSql(final StringBuilder filterSql, final List<Object> paramList, final Filter filter)
            throws RepositoryException {
        if (null == filter) {
            return;
        }

        if (filter instanceof PropertyFilter) {
            processPropertyFilter(filterSql, paramList, (PropertyFilter) filter);
        } else { // CompositeFiler
            processCompositeFilter(filterSql, paramList, (CompositeFilter) filter);
        }
    }

    /**
     * 
     * getOrderBySql.
     * 
     * @param orderBySql orderBySql
     * @param sorts sorts
     */
    private void getOrderBySql(final StringBuilder orderBySql, final Map<String, SortDirection> sorts) {
        boolean isFirst = true;
        String querySortDirection;
        for (final Map.Entry<String, SortDirection> sort : sorts.entrySet()) {
            if (isFirst) {
                orderBySql.append(" order by ");
                isFirst = false;
            } else {
                orderBySql.append(",");
            }

            if (sort.getValue().equals(SortDirection.ASCENDING)) {
                querySortDirection = "asc";
            } else {
                querySortDirection = "desc";
            }

            orderBySql.append(sort.getKey()).append(" ").append(querySortDirection);
        }
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        final List<JSONObject> jsonObjects = new ArrayList<JSONObject>();

        final StringBuilder sql = new StringBuilder();
        JSONArray jsonArray;

        final Connection connection = getConnection();
        getRandomly(fetchSize, sql);
        try {
            jsonArray = JdbcUtil.queryJsonArray(sql.toString(), new ArrayList<Object>(), connection, getName());

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(jsonArray.getJSONObject(i));
            }
        } catch (final SQLException se) {
            LOGGER.log(Level.SEVERE, "update:" + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "getRandomly:" + e.getMessage(), e);
            throw new RepositoryException(e);
        } finally {
            closeQueryConnection(connection);
        }

        return jsonObjects;
    }

    /**
     * getRandomly.
     * 
     * @param fetchSize fetchSize
     * @param sql sql
     */
    private void getRandomly(final int fetchSize, final StringBuilder sql) {
        sql.append(JdbcFactory.createJdbcFactory().getRandomlySql(getName(), fetchSize));
    }

    @Override
    public long count() throws RepositoryException {
        final String cacheKey = CACHE_KEY_PREFIX + getName() + REPOSITORY_CACHE_COUNT;
        if (cacheEnabled) {
            final Object o = CACHE.get(cacheKey);
            if (null != o) {
                LOGGER.log(Level.FINER, "Got an object[cacheKey={0}] from repository cache[name={1}]", new Object[]{cacheKey, getName()});
                try {
                    return (Long) o;
                } catch (final Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);

                    return -1;
                }
            }
        }

        final StringBuilder sql = new StringBuilder("select count(" + JdbcRepositories.OID + ") from ").append(getName());
        final long ret = count(sql, new ArrayList<Object>());

        if (cacheEnabled) {
            CACHE.putAsync(cacheKey, ret);
            LOGGER.log(Level.FINER, "Added an object[cacheKey={0}] in repository cache[{1}]", new Object[]{cacheKey, getName()});
        }

        return ret;
    }

    /**
     * count.
     * 
     * @param sql sql
     * @param paramList paramList
     * @return count
     * @throws RepositoryException RepositoryException
     */
    private long count(final StringBuilder sql, final List<Object> paramList) throws RepositoryException {
        final Connection connection = getConnection();

        JSONObject jsonObject;
        long count;
        try {
            jsonObject = JdbcUtil.queryJsonObject(sql.toString(), paramList, connection, getName());
            count = jsonObject.getLong(jsonObject.keys().next().toString());
        } catch (final SQLException se) {
            LOGGER.log(Level.SEVERE, "update:"
                                     + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "count :"
                                     + e.getMessage(), e);
            throw new RepositoryException(e);
        } finally {
            closeQueryConnection(connection);
        }

        return count;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Transaction beginTransaction() {
        final JdbcTransaction ret = TX.get();
        if (null != ret) {
            LOGGER.log(Level.FINER, "There is a transaction[isActive={0}] in current thread", ret.isActive());
            if (ret.isActive()) {
                return TX.get(); // Using 'the current transaction'
            }
        }

        JdbcTransaction jdbcTransaction = null;
        try {
            jdbcTransaction = new JdbcTransaction();
        } catch (final SQLException e) {
            LOGGER.severe("init jdbcTransaction wrong");
        }

        TX.set(jdbcTransaction);

        return jdbcTransaction;
    }

    @Override
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    @Override
    public void setCacheEnabled(final boolean isCacheEnabled) {
        cacheEnabled = isCacheEnabled;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public void setWritable(final boolean writable) {
        this.writable = writable;
    }

    @Override
    public Cache<String, Serializable> getCache() {
        // TODO Auto-generated method stub
        return CACHE;
    }

    /**
     * Constructs a JDBC repository with the specified name.
     * 
     * @param name the specified name
     */
    public JdbcRepository(final String name) {
        this.name = name;
    }

    /**
     * dispose the resource when requestDestroyed .
     */
    public static void dispose() {
        final JdbcTransaction jdbcTransaction = TX.get();
        if (jdbcTransaction == null) {
            return;
        }

        if (jdbcTransaction.getConnection() != null) {
            jdbcTransaction.dispose();
        }

    }

    /**
     * getConnection.
     * default using current JdbcTransaction's connection,if null get a new one.
     * 
     * @return {@link Connection}
     */
    private Connection getConnection() {
        final JdbcTransaction jdbcTransaction = TX.get();
        if (jdbcTransaction == null || !jdbcTransaction.isActive()) {
            try {
                return Connections.getConnection();
            } catch (final SQLException e) {
                LOGGER.log(Level.SEVERE, "Gets connection error", e);
            }
        }

        return jdbcTransaction.getConnection();
    }

    /**
     * closeQueryConnection,this connection not in JdbcTransaction,
     * should be closed in code.
     * 
     * @param connection {@link Connection}
     * @throws RepositoryException  RepositoryException
    
     */
    private void closeQueryConnection(final Connection connection) throws RepositoryException {
        final JdbcTransaction jdbcTransaction = TX.get();
        if (jdbcTransaction == null || !jdbcTransaction.isActive()) {
            try {
                connection.close();
            } catch (final SQLException e) {
                LOGGER.log(Level.SEVERE, "Can not close database connection", e);
                throw new RepositoryException(e);
            }
        } else {
            LOGGER.log(Level.FINEST, "Do not close query connection caused by in a transaction");
        }
    }

    /**
     * Processes property filter.
     * 
     * @param filterSql the specified filter SQL to build
     * @param paramList the specified parameter list
     * @param propertyFilter the specified property filter
     * @throws RepositoryException repository exception
     */
    private void processPropertyFilter(final StringBuilder filterSql, final List<Object> paramList, final PropertyFilter propertyFilter)
            throws RepositoryException {
        String filterOperator = null;

        switch (propertyFilter.getOperator()) {
            case EQUAL:
                filterOperator = "=";
                break;
            case GREATER_THAN:
                filterOperator = ">";
                break;
            case GREATER_THAN_OR_EQUAL:
                filterOperator = ">=";
                break;
            case LESS_THAN:
                filterOperator = "<";
                break;
            case LESS_THAN_OR_EQUAL:
                filterOperator = "<=";
                break;
            case NOT_EQUAL:
                filterOperator = "!=";
                break;
            case IN:
                filterOperator = "in";
                break;
            default:
                throw new RepositoryException("Unsupported filter operator[" + propertyFilter.getOperator() + "]");
        }

        if (FilterOperator.IN != propertyFilter.getOperator()) {
            filterSql.append(propertyFilter.getKey()).append(filterOperator).append("?");
            paramList.add(propertyFilter.getValue());
        } else {
            final Collection<Object> objects = (Collection<Object>) propertyFilter.getValue();

            boolean isSubFist = true;
            if (objects != null && objects.size() > 0) {
                filterSql.append(propertyFilter.getKey()).append(" in ");

                final Iterator<Object> obs = objects.iterator();
                while (obs.hasNext()) {
                    if (isSubFist) {
                        filterSql.append("(");
                        isSubFist = false;
                    } else {
                        filterSql.append(",");
                    }
                    filterSql.append("?");
                    paramList.add(obs.next());

                    if (!obs.hasNext()) {
                        filterSql.append(") ");
                    }
                }
            }
        }
    }

    /**
     * Processes composite filter.
     * 
     * @param filterSql the specified filter SQL to build
     * @param paramList the specified parameter list
     * @param compositeFilter the specified composite filter
     * @throws RepositoryException repository exception
     */
    private void processCompositeFilter(final StringBuilder filterSql, final List<Object> paramList,
                                        final CompositeFilter compositeFilter) throws RepositoryException {
        final List<Filter> subFilters = compositeFilter.getSubFilters();

        if (2 > subFilters.size()) {
            throw new RepositoryException("At least two sub filters in a composite filter");
        }

        filterSql.append("(");

        final Iterator<Filter> iterator = subFilters.iterator();
        while (iterator.hasNext()) {
            final Filter filter = iterator.next();

            if (filter instanceof PropertyFilter) {
                processPropertyFilter(filterSql, paramList, (PropertyFilter) filter);
            } else { // CompositeFilter
                processCompositeFilter(filterSql, paramList, (CompositeFilter) filter);
            }

            if (iterator.hasNext()) {
                switch (compositeFilter.getOperator()) {
                    case AND:
                        filterSql.append(" and ");
                        break;
                    case OR:
                        filterSql.append(" or ");
                        break;
                    default:
                        throw new RepositoryException("Unsupported composite filter[operator=" + compositeFilter.getOperator() + "]");
                }
            }
        }

        filterSql.append(")");
    }
}
