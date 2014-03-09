/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.CompositeFilter;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Projection;
import org.b3log.latke.repository.PropertyFilter;
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
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.7, Oct 12, 2013
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
     * Writable?
     */
    private boolean writable = true;

    /**
     * Repository cache name.
     */
    public static final String REPOSITORY_CACHE_NAME = "repositoryCache";

    /**
     * The current transaction.
     */
    public static final ThreadLocal<JdbcTransaction> TX = new InheritableThreadLocal<JdbcTransaction>();

    /**
     * The current JDBC connection.
     */
    public static final ThreadLocal<Connection> CONN = new ThreadLocal<Connection>();

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
            LOGGER.log(Level.ERROR, "add:" + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "add:" + e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return id;
    }

    /**
     * buildAddSql.
     *
     * @param jsonObject jsonObject
     * @param paramlist paramlist
     * @param sql sql
     * @return id
     * @throws Exception exception
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
     * @param jsonObject
     * jsonObject
     * @param paramlist
     * paramlist
     * @param sql
     * sql
     * @throws Exception
     * exception
     */
    private void setProperties(final JSONObject jsonObject, final List<Object> paramlist, final StringBuilder sql)
        throws Exception {
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

        sql.append("insert into ").append(getName()).append(insertString).append(" values ").append(wildcardString);
    }

    @Override
    public void update(final String id, final JSONObject jsonObject)
        throws RepositoryException {
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
            LOGGER.log(Level.ERROR, "update:" + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "update:" + e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    /**
     *
     * update.
     *
     * @param id
     * id
     * @param oldJsonObject
     * oldJsonObject
     * @param jsonObject
     * newJsonObject
     * @param paramList
     * paramList
     * @param sql
     * sql
     * @throws JSONException
     * JSONException
     */
    private void update(final String id, final JSONObject oldJsonObject,
        final JSONObject jsonObject, final List<Object> paramList,
        final StringBuilder sql) throws JSONException {
        final JSONObject needUpdateJsonObject = getNeedUpdateJsonObject(oldJsonObject, jsonObject);

        if (needUpdateJsonObject.length() == 0) {
            LOGGER.log(Level.INFO, "nothing to update [{0}] for repository [{1}]", new Object[] {id, getName()});
            return;
        }

        setUpdateProperties(id, needUpdateJsonObject, paramList, sql);
    }

    /**
     * setUpdateProperties.
     *
     * @param id
     * id
     * @param needUpdateJsonObject
     * needUpdateJsonObject
     * @param paramList
     * paramList
     * @param sql
     * sql
     * @throws JSONException
     * JSONException
     */
    private void setUpdateProperties(final String id,
        final JSONObject needUpdateJsonObject,
        final List<Object> paramList, final StringBuilder sql)
        throws JSONException {
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

        sql.append("update ").append(getName()).append(wildcardString).append(" where ").append(JdbcRepositories.getDefaultKeyName()).append("=").append(
            "?");
        paramList.add(id);
    }

    /**
     *
     * getNeedUpdateJsonObject.
     *
     * @param oldJsonObject
     * oldJsonObject
     * @param jsonObject
     * newJsonObject
     * @return JSONObject
     * @throws JSONException
     * jsonObject
     */
    private JSONObject getNeedUpdateJsonObject(final JSONObject oldJsonObject, final JSONObject jsonObject) throws JSONException {
        if (null == oldJsonObject) {
            return jsonObject;
        }

        final JSONObject needUpdateJsonObject = new JSONObject();

        final Iterator<String> keys = jsonObject.keys();

        String key;

        while (keys.hasNext()) {
            key = keys.next();

            if (jsonObject.get(key) == null && oldJsonObject.get(key) == null) {
                // ???????????????????????????
                needUpdateJsonObject.put(key, jsonObject.get(key));
            } else if (!jsonObject.optString(key).equals(oldJsonObject.optString(key))) {
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
            LOGGER.log(Level.ERROR, "remove:" + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "remove:" + e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Removes an record.
     *
     * @param id id
     * @param sql sql
     */
    private void remove(final String id, final StringBuilder sql) {
        sql.append("delete from ").append(getName()).append(" where ").append(JdbcRepositories.getDefaultKeyName()).append("='").append(id).append(
            "'");
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret = null;

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();

        try {
            get(sql);
            final ArrayList<Object> paramList = new ArrayList<Object>();

            paramList.add(id);
            ret = JdbcUtil.queryJsonObject(sql.toString(), paramList, connection, getName());
        } catch (final SQLException e) {
            throw new JDBCRepositoryException(e);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "get:" + e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * get.
     *
     * @param sql sql
     */
    private void get(final StringBuilder sql) {
        sql.append("select * from ").append(getName()).append(" where ").append(JdbcRepositories.getDefaultKeyName()).append("=").append("?");
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        final Map<String, JSONObject> map = new HashMap<String, JSONObject>();
        JSONObject jsonObject;

        for (final String id : ids) {
            jsonObject = get(id);
            map.put(jsonObject.optString(JdbcRepositories.getDefaultKeyName()), jsonObject);
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
        final JSONObject ret = new JSONObject();

        final int currentPageNum = query.getCurrentPageNum();
        final int pageSize = query.getPageSize();

        // Asssumes the application call need to count page
        int pageCount = -1;

        // If the application caller dose NOT want to count page, gets the page count the caller specified
        if (null != query.getPageCount()) {
            pageCount = query.getPageCount();
        }

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<Object>();

        try {
            final Map<String, Object> paginationCnt = get(currentPageNum, pageSize, pageCount, query, sql, paramList);

            // page
            final JSONObject pagination = new JSONObject();

            final int pageCnt = (Integer) paginationCnt.get(Pagination.PAGINATION_PAGE_COUNT);

            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
            pagination.put(Pagination.PAGINATION_RECORD_COUNT, paginationCnt.get(Pagination.PAGINATION_RECORD_COUNT));

            ret.put(Pagination.PAGINATION, pagination);

            // result
            if (0 == pageCnt) {
                ret.put(Keys.RESULTS, new JSONArray());
                return ret;
            }

            final JSONArray jsonResults = JdbcUtil.queryJsonArray(sql.toString(), paramList, connection, getName());

            ret.put(Keys.RESULTS, jsonResults);
        } catch (final SQLException e) {
            throw new JDBCRepositoryException(e);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "query: " + e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * getQuery sql.
     *
     * @param currentPageNum currentPageNum
     * @param pageSize pageSize
     * @param pageCount if the pageCount specified with {@code -1}, the returned (pageCnt, recordCnt) value will be calculated, otherwise,
     * the returned pageCnt will be this pageCount, and recordCnt will be {@code 0}, means these values will not be calculated
     * @param query query
     * @param sql sql
     * @param paramList paramList
     * @return &lt;pageCnt, Integer&gt;,<br/>
     * &lt;recordCnt, Integer&gt;<br/>
     * @throws RepositoryException RepositoryException
     */
    private Map<String, Object> get(final int currentPageNum, final int pageSize, final int pageCount,
        final Query query, final StringBuilder sql, final List<Object> paramList) throws RepositoryException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        int pageCnt = pageCount;
        int recordCnt = 0;

        final StringBuilder selectSql = new StringBuilder();
        final StringBuilder filterSql = new StringBuilder();
        final StringBuilder orderBySql = new StringBuilder();

        getSelectSql(selectSql, query.getProjections());
        getFilterSql(filterSql, paramList, query.getFilter());
        getOrderBySql(orderBySql, query.getSorts());

        if (-1 == pageCount) {
            final StringBuilder countSql = new StringBuilder("select count(" + JdbcRepositories.getDefaultKeyName() + ") from ").append(
                getName());

            if (StringUtils.isNotBlank(filterSql.toString())) {
                countSql.append(" where ").append(filterSql);
            }

            recordCnt = (int) count(countSql, paramList);

            if (0 == recordCnt) {
                ret.put(Pagination.PAGINATION_PAGE_COUNT, 0);
                ret.put(Pagination.PAGINATION_RECORD_COUNT, 0);

                return ret;
            }

            pageCnt = (int) Math.ceil((double) recordCnt / (double) pageSize);
        }

        ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
        ret.put(Pagination.PAGINATION_RECORD_COUNT, recordCnt);

        if (currentPageNum > pageCnt) {
            LOGGER.log(Level.WARN, "Current page num [{0}] > page count [{1}]", new Object[] {currentPageNum, pageCnt});
        }

        getQuerySql(currentPageNum, pageSize, selectSql, filterSql, orderBySql, sql);

        return ret;
    }

    /**
     * get select sql. if projections size = 0 ,return select count(*).
     *
     * @param selectSql
     * selectSql
     * @param projections
     * projections
     */
    private void getSelectSql(final StringBuilder selectSql,
        final Set<Projection> projections) {
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
     * @param currentPageNum
     * currentPageNum
     * @param pageSize
     * pageSize
     * @param selectSql
     * selectSql
     * @param filterSql
     * filterSql
     * @param orderBySql
     * orderBySql
     * @param sql
     * sql
     */
    private void getQuerySql(final int currentPageNum, final int pageSize,
        final StringBuilder selectSql, final StringBuilder filterSql,
        final StringBuilder orderBySql, final StringBuilder sql) {
        final int start = (currentPageNum - 1) * pageSize;
        final int end = start + pageSize;

        sql.append(
            JdbcFactory.createJdbcFactory().queryPage(start, end, selectSql.toString(), filterSql.toString(), orderBySql.toString(),
            getName()));
    }

    /**
     *
     * get filterSql and paramList.
     *
     * @param filterSql
     * filterSql
     * @param paramList
     * paramList
     * @param filter
     * filter
     * @throws RepositoryException
     * RepositoryException
     */
    private void getFilterSql(final StringBuilder filterSql,
        final List<Object> paramList, final Filter filter)
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
     * @param orderBySql
     * orderBySql
     * @param sorts
     * sorts
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
    public List<JSONObject> getRandomly(final int fetchSize)
        throws RepositoryException {
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
            LOGGER.log(Level.ERROR, "getRandomly:" + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "getRandomly:" + e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return jsonObjects;
    }

    /**
     * getRandomly.
     *
     * @param fetchSize
     * fetchSize
     * @param sql
     * sql
     */
    private void getRandomly(final int fetchSize, final StringBuilder sql) {
        sql.append(JdbcFactory.createJdbcFactory().getRandomlySql(getName(), fetchSize));
    }

    @Override
    public long count() throws RepositoryException {
        final StringBuilder sql = new StringBuilder("select count(" + JdbcRepositories.getDefaultKeyName() + ") from ").append(getName());

        return count(sql, new ArrayList<Object>());
    }

    /**
     * count.
     *
     * @param sql
     * sql
     * @param paramList
     * paramList
     * @return count
     * @throws RepositoryException
     * RepositoryException
     */
    private long count(final StringBuilder sql, final List<Object> paramList) throws RepositoryException {
        final Connection connection = getConnection();

        JSONObject jsonObject;
        long count;

        try {
            jsonObject = JdbcUtil.queryJsonObject(sql.toString(), paramList, connection, getName());
            count = jsonObject.getLong(jsonObject.keys().next().toString());
        } catch (final SQLException se) {
            LOGGER.log(Level.ERROR, "count:" + se.getMessage(), se);
            throw new JDBCRepositoryException(se);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "count :" + e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return count;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <b>Note</b>: The returned name maybe with table name prefix.
     * </p>
     */
    @Override
    public String getName() {
        final String tableNamePrefix = StringUtils.isNotBlank(Latkes.getLocalProperty("jdbc.tablePrefix"))
            ? Latkes.getLocalProperty("jdbc.tablePrefix") + "_"
            : "";

        return tableNamePrefix + name;
    }

    @Override
    public Transaction beginTransaction() {
        final JdbcTransaction ret = TX.get();

        if (null != ret) {
            LOGGER.log(Level.DEBUG, "There is a transaction[isActive={0}] in current thread", ret.isActive());
            if (ret.isActive()) {
                return TX.get(); // Using 'the current transaction'
            }
        }

        JdbcTransaction jdbcTransaction = null;

        try {
            jdbcTransaction = new JdbcTransaction();
        } catch (final SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to initialize JDBC transaction", e);

            throw new IllegalStateException("Failed to initialize JDBC transaction");
        }

        TX.set(jdbcTransaction);

        return jdbcTransaction;
    }

    @Override
    public boolean hasTransactionBegun() {
        return null != TX.get();
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

        if (null != jdbcTransaction && jdbcTransaction.getConnection() != null) {
            jdbcTransaction.dispose();
        }

        final Connection connection = CONN.get();

        if (null != connection) {
            try {
                connection.close();
            } catch (final SQLException e) {
                throw new RuntimeException("Close connection failed", e);
            } finally {
                CONN.set(null);
            }
        }
    }

    /**
     * getConnection. default using current JdbcTransaction's connection,if null
     * get a new one.
     *
     * @return {@link Connection}
     */
    private Connection getConnection() {
        final JdbcTransaction jdbcTransaction = TX.get();

        if (null != jdbcTransaction && jdbcTransaction.isActive()) {
            return jdbcTransaction.getConnection();
        }

        Connection ret = CONN.get();

        try {
            if (null != ret && !ret.isClosed()) {
                return ret;
            }

            ret = Connections.getConnection();

            CONN.set(ret);
        } catch (final SQLException e) {
            LOGGER.log(Level.ERROR, "Gets connection failed", e);
        }

        return ret;
    }

    /**
     * Processes property filter.
     *
     * @param filterSql the specified filter SQL to build
     * @param paramList the specified parameter list
     * @param propertyFilter the specified property filter
     * @throws RepositoryException repository exception
     */
    private void processPropertyFilter(final StringBuilder filterSql,
        final List<Object> paramList, final PropertyFilter propertyFilter)
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

        case LIKE:
            filterOperator = " like ";
            break;

        default:
            throw new RepositoryException("Unsupported filter operator [" + propertyFilter.getOperator() + "]");
        }

        if (FilterOperator.IN != propertyFilter.getOperator()) {
            filterSql.append(propertyFilter.getKey()).append(filterOperator).append("?");
            paramList.add(propertyFilter.getValue());
        } else {
            final Collection<Object> objects = (Collection<Object>) propertyFilter.getValue();

            boolean isSubFist = true;

            if (objects != null && !objects.isEmpty()) {
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
            } else { // in () => 1!=1
                filterSql.append("1!=1");
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
    private void processCompositeFilter(final StringBuilder filterSql,
        final List<Object> paramList, final CompositeFilter compositeFilter)
        throws RepositoryException {
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
                    throw new RepositoryException("Unsupported composite filter [operator=" + compositeFilter.getOperator() + "]");
                }
            }
        }

        filterSql.append(")");
    }
}
