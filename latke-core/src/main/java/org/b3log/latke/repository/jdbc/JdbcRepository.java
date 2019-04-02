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
package org.b3log.latke.repository.jdbc;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.JdbcUtil;
import org.b3log.latke.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JDBC repository implementation.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.4, Nov 4, 2018
 */
public final class JdbcRepository implements Repository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcRepository.class);

    /**
     * The current transaction.
     */
    public static final ThreadLocal<JdbcTransaction> TX = new ThreadLocal<>();

    /**
     * The current JDBC connection.
     */
    public static final ThreadLocal<Connection> CONN = new ThreadLocal<>();

    /**
     * Key generator.
     */
    private static final KeyGenerator<?> KEY_GEN;

    static {
        final String value = Latkes.getLocalProperty("keyGen");
        if (StringUtils.isBlank(value) || "org.b3log.latke.repository.TimeMillisKeyGenerator".equals(value)) {
            KEY_GEN = new TimeMillisKeyGenerator();
        } else if ("DB".equals(value)) {
            KEY_GEN = new DBKeyGenerator();
        } else { // User customized key generator
            try {
                final Class<?> keyGenClass = Class.forName(value);
                final Constructor<?> constructor = keyGenClass.getConstructor();

                KEY_GEN = (KeyGenerator) constructor.newInstance();

            } catch (final Exception e) {
                throw new IllegalArgumentException("Can not load key generator with the specified class name [" + value + ']', e);
            }
        }
    }

    /**
     * Repository name.
     */
    private final String name;

    /**
     * Writable?
     */
    private boolean writable = true;

    /**
     * Debug enabled?
     */
    private boolean debug;

    /**
     * Constructs a JDBC repository with the specified name.
     *
     * @param name the specified name
     */
    public JdbcRepository(final String name) {
        this.name = name;
    }

    /**
     * Disposes the resources.
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

    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        final JdbcTransaction currentTransaction = TX.get();
        if (null == currentTransaction) {
            throw new RepositoryException("Invoking add() outside a transaction");
        }

        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<>();
        final StringBuilder sql = new StringBuilder();
        String ret;

        try {
            if (Latkes.RuntimeDatabase.ORACLE == Latkes.getRuntimeDatabase()) {
                toOracleClobEmpty(jsonObject);
            }
            ret = buildAddSql(jsonObject, paramList, sql);
            JdbcUtil.executeSql(sql.toString(), paramList, connection, debug);
            JdbcUtil.fromOracleClobEmpty(jsonObject);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Add failed", e);

            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * buildAddSql.
     *
     * @param jsonObject jsonObject
     * @param paramlist  paramlist
     * @param sql        sql
     * @return id
     * @throws Exception exception
     */
    private String buildAddSql(final JSONObject jsonObject, final List<Object> paramlist, final StringBuilder sql) throws Exception {
        String ret = null;

        if (!jsonObject.has(Keys.OBJECT_ID)) {
            if (!(KEY_GEN instanceof DBKeyGenerator)) {
                ret = (String) KEY_GEN.gen();
                jsonObject.put(Keys.OBJECT_ID, ret);
            }
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
     * @param paramlist  paramlist
     * @param sql        sql
     */
    private void setProperties(final JSONObject jsonObject, final List<Object> paramlist, final StringBuilder sql) {
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
    public void update(final String id, final JSONObject jsonObject) throws RepositoryException {
        if (StringUtils.isBlank(id)) {
            return;
        }

        final JdbcTransaction currentTransaction = TX.get();
        if (null == currentTransaction) {
            throw new RepositoryException("Invoking update() outside a transaction");
        }

        final JSONObject oldJsonObject = get(id);

        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<>();
        final StringBuilder sqlBuilder = new StringBuilder();

        try {
            if (Latkes.RuntimeDatabase.ORACLE == Latkes.getRuntimeDatabase()) {
                toOracleClobEmpty(jsonObject);
            }
            update(id, oldJsonObject, jsonObject, paramList, sqlBuilder);
            JdbcUtil.fromOracleClobEmpty(jsonObject);

            final String sql = sqlBuilder.toString();
            if (StringUtils.isBlank(sql)) {
                return;
            }

            JdbcUtil.executeSql(sql, paramList, connection, debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Update failed", e);

            throw new RepositoryException(e);
        }
    }

    /**
     * Compares the specified old json object and new json object, updates it if need.
     *
     * @param id            id
     * @param oldJsonObject the specified old json object
     * @param jsonObject    the specified new json object
     * @param paramList     paramList
     * @param sql           sql
     * @throws JSONException JSONException
     */
    private void update(final String id, final JSONObject oldJsonObject, final JSONObject jsonObject,
                        final List<Object> paramList, final StringBuilder sql) throws JSONException {
        final JSONObject needUpdateJsonObject = getNeedUpdateJsonObject(oldJsonObject, jsonObject);
        if (0 == needUpdateJsonObject.length()) {
            LOGGER.log(Level.TRACE, "Nothing to update [{0}] for repository [{1}]", id, getName());

            return;
        }

        setUpdateProperties(id, needUpdateJsonObject, paramList, sql);
    }

    /**
     * setUpdateProperties.
     *
     * @param id                   id
     * @param needUpdateJsonObject needUpdateJsonObject
     * @param paramList            paramList
     * @param sql                  sql
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
                wildcardString.append(" SET ").append(key).append(" = ?");
                isFirst = false;
            } else {
                wildcardString.append(", ").append(key).append(" = ?");
            }

            paramList.add(needUpdateJsonObject.get(key));
        }

        sql.append("UPDATE ").append(getName()).append(wildcardString).append(" WHERE ").append(JdbcRepositories.getDefaultKeyName()).append(" = ?");
        paramList.add(id);
    }

    /**
     * Compares the specified old json object and the new json object, returns diff object for updating.
     *
     * @param oldJsonObject the specified old json object
     * @param jsonObject    the specified new json object
     * @return diff object for updating
     * @throws JSONException jsonObject
     */
    private JSONObject getNeedUpdateJsonObject(final JSONObject oldJsonObject, final JSONObject jsonObject) throws JSONException {
        if (null == oldJsonObject) {
            return jsonObject;
        }

        final JSONObject ret = new JSONObject();
        final Iterator<String> keys = jsonObject.keys();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            if (null == jsonObject.get(key) && null == oldJsonObject.get(key)) {
                ret.put(key, jsonObject.get(key));
            } else if (!jsonObject.optString(key).equals(oldJsonObject.optString(key))) {
                ret.put(key, jsonObject.get(key));
            }
        }

        return ret;
    }

    @Override
    public void remove(final String id) throws RepositoryException {
        if (StringUtils.isBlank(id)) {
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
            JdbcUtil.executeSql(sql.toString(), connection, debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Remove failed", e);

            throw new RepositoryException(e);
        }
    }

    @Override
    public void remove(final Query query) throws RepositoryException {
        final StringBuilder deleteSQL = new StringBuilder("DELETE FROM ").append(getName());

        final List<Object> paramList = new ArrayList<>();
        final StringBuilder filterSql = new StringBuilder();
        buildWhere(filterSql, paramList, query.getFilter());
        if (StringUtils.isNotBlank(filterSql.toString())) {
            deleteSQL.append(" WHERE ").append(filterSql);
        }

        final Connection connection = getConnection();
        try {
            JdbcUtil.executeSql(deleteSQL.toString(), paramList, connection, debug);
        } catch (final SQLException e) {
            LOGGER.log(Level.ERROR, "Remove failed", e);

            throw new RepositoryException(e);
        }
    }

    /**
     * Removes an record.
     *
     * @param id  id
     * @param sql sql
     */
    private void remove(final String id, final StringBuilder sql) {
        sql.append("DELETE FROM ").append(getName()).append(" WHERE ").append(JdbcRepositories.getDefaultKeyName()).append(" = '").
                append(id).append("'");
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret;

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();

        try {
            get(sql);
            final ArrayList<Object> paramList = new ArrayList<>();

            paramList.add(id);
            ret = JdbcUtil.queryJsonObject(sql.toString(), paramList, connection, getName(), debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get failed", e);

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
        sql.append("SELECT * FROM ").append(getName()).append(" WHERE ").append(JdbcRepositories.getDefaultKeyName()).append(" = ?");
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        final Map<String, JSONObject> map = new HashMap<>();
        JSONObject jsonObject;

        for (final String id : ids) {
            jsonObject = get(id);
            map.put(jsonObject.optString(JdbcRepositories.getDefaultKeyName()), jsonObject);
        }

        return map;
    }

    @Override
    public boolean has(final String id) throws RepositoryException {
        return null != get(id);
    }

    @Override
    public JSONObject get(final Query query) throws RepositoryException {
        final JSONObject ret = new JSONObject();

        final int currentPageNum = query.getCurrentPageNum();
        final int pageSize = query.getPageSize();

        // Assumes the application call need to count page
        int pageCount = -1;

        // If the application caller dose NOT want to count page, gets the page count the caller specified
        if (null != query.getPageCount()) {
            pageCount = query.getPageCount();
        }

        final StringBuilder sql = new StringBuilder();
        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<>();

        try {
            final Map<String, Object> paginationCnt = buildSQLCount(currentPageNum, pageSize, pageCount, query, sql, paramList);
            final JSONObject pagination = new JSONObject();
            final int pageCnt = (Integer) paginationCnt.get(Pagination.PAGINATION_PAGE_COUNT);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
            pagination.put(Pagination.PAGINATION_RECORD_COUNT, paginationCnt.get(Pagination.PAGINATION_RECORD_COUNT));
            ret.put(Pagination.PAGINATION, pagination);
            if (0 == pageCnt) {
                ret.put(Keys.RESULTS, new JSONArray());

                return ret;
            }

            final JSONArray jsonResults = JdbcUtil.queryJsonArray(sql.toString(), paramList, connection, getName(), query.isDebug());

            ret.put(Keys.RESULTS, jsonResults);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Query failed", e);

            throw new RepositoryException(e);
        }

        return ret;
    }

    @Override
    public List<JSONObject> select(final String statement, final Object... params) throws RepositoryException {
        JSONArray jsonResults;

        final Connection connection = getConnection();
        try {
            if (null == params || 0 == params.length) {
                jsonResults = JdbcUtil.queryJsonArray(statement, Collections.emptyList(), connection, getName(), debug);
            } else {
                jsonResults = JdbcUtil.queryJsonArray(statement, Arrays.asList(params), connection, getName(), debug);
            }

            return CollectionUtils.jsonArrayToList(jsonResults);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Select failed", e);

            throw new RepositoryException(e);
        }
    }

    /**
     * Builds query SQL and count result.
     *
     * @param currentPageNum currentPageNum
     * @param pageSize       pageSize
     * @param pageCount      if the pageCount specified with {@code -1}, the returned (pageCnt, recordCnt) value will be
     *                       calculated, otherwise, the returned pageCnt will be this pageCount, and recordCnt will be {@code 0}, means these
     *                       values will not be calculated
     * @param query          query
     * @param sqlBuilder     the specified SQL builder
     * @param paramList      paramList
     * @return &lt;pageCnt, Integer&gt;,<br/>
     * &lt;recordCnt, Integer&gt;<br/>
     * @throws RepositoryException RepositoryException
     */
    private Map<String, Object> buildSQLCount(final int currentPageNum, final int pageSize, final int pageCount,
                                              final Query query, final StringBuilder sqlBuilder, final List<Object> paramList) throws RepositoryException {
        final Map<String, Object> ret = new HashMap<>();

        int pageCnt = pageCount;
        int recordCnt = 0;

        final StringBuilder selectBuilder = new StringBuilder();
        final StringBuilder whereBuilder = new StringBuilder();
        final StringBuilder orderByBuilder = new StringBuilder();

        buildSelect(selectBuilder, query.getProjections());
        buildWhere(whereBuilder, paramList, query.getFilter());
        buildOrderBy(orderByBuilder, query.getSorts());

        if (-1 == pageCount) {
            final StringBuilder countBuilder = new StringBuilder("SELECT COUNT(" + JdbcRepositories.getDefaultKeyName() + ") FROM ").append(getName());
            if (StringUtils.isNotBlank(whereBuilder.toString())) {
                countBuilder.append(" WHERE ").append(whereBuilder);
            }
            recordCnt = (int) count(countBuilder, paramList);
            if (0 == recordCnt) {
                ret.put(Pagination.PAGINATION_PAGE_COUNT, 0);
                ret.put(Pagination.PAGINATION_RECORD_COUNT, 0);

                return ret;
            }

            pageCnt = (int) Math.ceil((double) recordCnt / (double) pageSize);
        }

        ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
        ret.put(Pagination.PAGINATION_RECORD_COUNT, recordCnt);

        final int start = (currentPageNum - 1) * pageSize;
        final int end = start + pageSize;
        sqlBuilder.append(JdbcFactory.getInstance().
                queryPage(start, end, selectBuilder.toString(), whereBuilder.toString(), orderByBuilder.toString(), getName()));

        return ret;
    }

    /**
     * Builds 'SELECT' part with the specified select build and projections.
     *
     * @param selectBuilder the specified select builder
     * @param projections   the specified projections
     */
    private void buildSelect(final StringBuilder selectBuilder, final List<Projection> projections) {
        selectBuilder.append("SELECT ");
        if (null == projections || projections.isEmpty()) {
            selectBuilder.append(" * ");

            return;
        }

        selectBuilder.append(projections.stream().map(Projection::getKey).collect(Collectors.joining(", ")));
    }


    /**
     * Builds 'WHERE' part with the specified where build, param list and filter.
     *
     * @param whereBuilder the specified where builder
     * @param paramList    the specified param list
     * @param filter       the specified filter
     * @throws RepositoryException RepositoryException
     */
    private void buildWhere(final StringBuilder whereBuilder, final List<Object> paramList, final Filter filter) throws RepositoryException {
        if (null == filter) {
            return;
        }

        if (filter instanceof PropertyFilter) {
            processPropertyFilter(whereBuilder, paramList, (PropertyFilter) filter);
        } else { // CompositeFiler
            processCompositeFilter(whereBuilder, paramList, (CompositeFilter) filter);
        }
    }

    /**
     * Builds 'ORDER BY' part with the specified order by build and sorts.
     *
     * @param orderByBuilder the specified order by builder
     * @param sorts          the specified sorts
     */
    private void buildOrderBy(final StringBuilder orderByBuilder, final Map<String, SortDirection> sorts) {
        boolean isFirst = true;
        String querySortDirection;

        for (final Map.Entry<String, SortDirection> sort : sorts.entrySet()) {
            if (isFirst) {
                orderByBuilder.append(" ORDER BY ");
                isFirst = false;
            } else {
                orderByBuilder.append(", ");
            }

            if (sort.getValue().equals(SortDirection.ASCENDING)) {
                querySortDirection = "ASC";
            } else {
                querySortDirection = "DESC";
            }
            orderByBuilder.append(sort.getKey()).append(" ").append(querySortDirection);
        }
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize) throws RepositoryException {
        final List<JSONObject> jsonObjects = new ArrayList<>();

        final StringBuilder sql = new StringBuilder();
        JSONArray jsonArray;

        final Connection connection = getConnection();

        getRandomly(fetchSize, sql);
        try {
            jsonArray = JdbcUtil.queryJsonArray(sql.toString(), new ArrayList<>(), connection, getName(), debug);

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(jsonArray.getJSONObject(i));
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get randomly failed", e);

            throw new RepositoryException(e);
        }

        return jsonObjects;
    }

    /**
     * getRandomly.
     *
     * @param fetchSize fetchSize
     * @param sql       sql
     */
    private void getRandomly(final int fetchSize, final StringBuilder sql) {
        sql.append(JdbcFactory.getInstance().getRandomlySql(getName(), fetchSize));
    }

    @Override
    public long count() throws RepositoryException {
        final StringBuilder sql = new StringBuilder("SELECT COUNT(" + JdbcRepositories.getDefaultKeyName() + ") FROM ").append(getName());

        return count(sql, new ArrayList<>());
    }

    @Override
    public long count(final Query query) throws RepositoryException {
        final StringBuilder countSql = new StringBuilder("SELECT COUNT(" + JdbcRepositories.getDefaultKeyName() + ") FROM ").append(getName());

        final List<Object> paramList = new ArrayList<>();
        final StringBuilder filterSql = new StringBuilder();

        buildWhere(filterSql, paramList, query.getFilter());

        if (StringUtils.isNotBlank(filterSql.toString())) {
            countSql.append(" WHERE ").append(filterSql);
        }

        return (int) count(countSql, paramList);
    }

    /**
     * Count.
     *
     * @param sql       sql
     * @param paramList paramList
     * @return count
     * @throws RepositoryException RepositoryException
     */
    private long count(final StringBuilder sql, final List<Object> paramList) throws RepositoryException {
        final Connection connection = getConnection();

        JSONObject jsonObject;
        long count;

        try {
            jsonObject = JdbcUtil.queryJsonObject(sql.toString(), paramList, connection, getName(), debug);
            count = jsonObject.getLong(jsonObject.keys().next());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Count failed", e);

            throw new RepositoryException(e);
        }

        return count;
    }

    /**
     * {@inheritDoc}
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
        JdbcTransaction ret = TX.get();
        if (null != ret && ret.isActive()) {
            ret.setProgrammatic(true);

            return TX.get(); // Using 'the current transaction'
        }

        try {
            ret = new JdbcTransaction();
            ret.setProgrammatic(true);
        } catch (final SQLException e) {
            LOGGER.log(Level.ERROR, "Failed to initialize JDBC transaction", e);

            throw new IllegalStateException("Begin a transaction failed");
        }

        TX.set(ret);

        return ret;
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

    @Override
    public void setDebug(final boolean debugEnabled) {
        this.debug = debugEnabled;
    }

    /**
     * getConnection. default using current JdbcTransaction's connection, if null get a new one.
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
        } catch (final SQLException e) {
            LOGGER.log(Level.ERROR, "Gets a connection failed", e);
        }

        CONN.set(ret);

        return ret;
    }

    /**
     * Processes property filter.
     *
     * @param whereBuilder   the specified where builder
     * @param paramList      the specified parameter list
     * @param propertyFilter the specified property filter
     * @throws RepositoryException repository exception
     */
    private void processPropertyFilter(final StringBuilder whereBuilder,
                                       final List<Object> paramList, final PropertyFilter propertyFilter) throws RepositoryException {
        String filterOperator;

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
                filterOperator = "IN";

                break;
            case LIKE:
                filterOperator = "LIKE";

                break;
            case NOT_LIKE:
                filterOperator = "NOT LIKE";

                break;
            default:
                throw new RepositoryException("Unsupported filter operator [" + propertyFilter.getOperator() + "]");
        }

        if (FilterOperator.IN != propertyFilter.getOperator()) {
            whereBuilder.append(propertyFilter.getKey()).append(" ").append(filterOperator).append(" ?");
            paramList.add(propertyFilter.getValue());
        } else {
            final Collection<Object> objects = (Collection<Object>) propertyFilter.getValue();

            boolean isSubFist = true;

            if (objects != null && !objects.isEmpty()) {
                whereBuilder.append(propertyFilter.getKey()).append(" IN ");

                final Iterator<Object> obs = objects.iterator();

                while (obs.hasNext()) {
                    if (isSubFist) {
                        whereBuilder.append("(");
                        isSubFist = false;
                    } else {
                        whereBuilder.append(",");
                    }
                    whereBuilder.append("?");
                    paramList.add(obs.next());

                    if (!obs.hasNext()) {
                        whereBuilder.append(") ");
                    }
                }
            } else { // in () => 1!=1
                whereBuilder.append("1 != 1");
            }
        }
    }

    /**
     * Processes composite filter.
     *
     * @param whereBuilder    the specified where builder
     * @param paramList       the specified parameter list
     * @param compositeFilter the specified composite filter
     * @throws RepositoryException repository exception
     */
    private void processCompositeFilter(final StringBuilder whereBuilder,
                                        final List<Object> paramList, final CompositeFilter compositeFilter) throws RepositoryException {
        final List<Filter> subFilters = compositeFilter.getSubFilters();
        if (2 > subFilters.size()) {
            throw new RepositoryException("At least two sub filters in a composite filter");
        }

        whereBuilder.append("(");

        final Iterator<Filter> iterator = subFilters.iterator();
        while (iterator.hasNext()) {
            final Filter filter = iterator.next();

            if (filter instanceof PropertyFilter) {
                processPropertyFilter(whereBuilder, paramList, (PropertyFilter) filter);
            } else { // CompositeFilter
                processCompositeFilter(whereBuilder, paramList, (CompositeFilter) filter);
            }

            if (iterator.hasNext()) {
                switch (compositeFilter.getOperator()) {
                    case AND:
                        whereBuilder.append(" AND ");
                        break;

                    case OR:
                        whereBuilder.append(" OR ");
                        break;

                    default:
                        throw new RepositoryException("Unsupported composite filter [operator=" + compositeFilter.getOperator() + "]");
                }
            }
        }

        whereBuilder.append(")");
    }

    /**
     * Placeholder for Oracle empty clob.
     */
    public static final String ORA_EMPTY_STR = "oranil";

    /**
     * Process Oracle CLOB empty string.
     *
     * @param jsonObject the specified JSON object
     */
    private static void toOracleClobEmpty(final JSONObject jsonObject) {
        final Iterator<String> keys = jsonObject.keys();
        try {
            while (keys.hasNext()) {
                final String name = keys.next();
                final Object val = jsonObject.get(name);
                if (val instanceof String) {
                    final String valStr = (String) val;
                    if (StringUtils.isBlank(valStr)) {
                        jsonObject.put(name, ORA_EMPTY_STR);
                    }
                }
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Process oracle clob empty failed", e);
        }
    }
}
