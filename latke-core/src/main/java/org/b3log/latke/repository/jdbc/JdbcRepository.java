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
package org.b3log.latke.repository.jdbc;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.JdbcUtil;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JDBC repository implementation.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Jun 20, 2020
 */
public final class JdbcRepository implements Repository {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(JdbcRepository.class);

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
                CONN.remove();
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
        final StringBuilder sqlBuilder = new StringBuilder();
        String ret;
        try {
            ret = buildAddSql(jsonObject, paramList, sqlBuilder);
            JdbcUtil.executeSql(sqlBuilder.toString(), paramList, connection, debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Adds a record failed", e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * Builds add param list and SQL.
     *
     * @param jsonObject jsonObject the specified JSON object
     * @param paramList  paramlist the specified param list
     * @param sqlBuilder the specified SQL builder
     * @return id the generated key
     */
    private String buildAddSql(final JSONObject jsonObject, final List<Object> paramList, final StringBuilder sqlBuilder) {
        String ret = null;
        if (!jsonObject.has(Keys.OBJECT_ID)) {
            if (!(KEY_GEN instanceof DBKeyGenerator)) {
                ret = (String) KEY_GEN.gen();
                jsonObject.put(Keys.OBJECT_ID, ret);
            }
        } else {
            ret = jsonObject.getString(Keys.OBJECT_ID);
        }

        final Iterator<String> keys = jsonObject.keys();
        final StringBuilder paraBuilder = new StringBuilder();
        final StringBuilder argBuilder = new StringBuilder();
        boolean isFirst = true;
        String key;
        Object value;
        while (keys.hasNext()) {
            key = keys.next();
            if (isFirst) {
                paraBuilder.append("(`").append(key).append("`");
                argBuilder.append("(?");
                isFirst = false;
            } else {
                paraBuilder.append(",`").append(key).append("`");
                argBuilder.append(",?");
            }

            value = jsonObject.get(key);
            paramList.add(value);

            if (!keys.hasNext()) {
                if (Repositories.isSoftDelete()) {
                    paraBuilder.append(", `").append(JdbcRepositories.softDeleteFieldName).append("`");
                    argBuilder.append(", 0");
                }
                paraBuilder.append(")");
                argBuilder.append(")");
            }
        }

        sqlBuilder.append("INSERT INTO ").append("`").append(getName()).append("`").append(paraBuilder).append(" VALUES ").append(argBuilder);
        return ret;
    }

    @Override
    public void update(final String id, final JSONObject jsonObject, final String... propertyNames) throws RepositoryException {
        if (StringUtils.isBlank(id)) {
            return;
        }

        final JdbcTransaction currentTransaction = TX.get();
        if (null == currentTransaction) {
            throw new RepositoryException("Invoking update() outside a transaction");
        }

        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<>();
        final StringBuilder sqlBuilder = new StringBuilder();
        try {
            final JSONObject oldJsonObject = get(id);
            buildUpdate(id, oldJsonObject, jsonObject, paramList, sqlBuilder, propertyNames);
            final String sql = sqlBuilder.toString();
            if (StringUtils.isBlank(sql)) {
                return;
            }

            JdbcUtil.executeSql(sql, paramList, connection, debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Updates a record [id=" + id + "] failed", e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Builds update param list and SQL.
     *
     * @param id            the specified id
     * @param oldJsonObject the specified old json object
     * @param jsonObject    the specified new json object
     * @param paramList     the specified param list
     * @param sqlBuilder    the specified SQL builder
     * @param propertyNames the specified property names
     */
    private void buildUpdate(final String id, final JSONObject oldJsonObject, final JSONObject jsonObject, final List<Object> paramList, final StringBuilder sqlBuilder, final String... propertyNames) {
        final JSONObject needUpdateJsonObject = getDiff(oldJsonObject, jsonObject, propertyNames);
        if (0 == needUpdateJsonObject.length()) {
            LOGGER.log(Level.TRACE, "Nothing to update [{}] for repository [{}]", id, getName());
            return;
        }

        final Iterator<String> keys = needUpdateJsonObject.keys();
        String key;
        boolean isFirst = true;
        final StringBuilder propertyBuilder = new StringBuilder();
        while (keys.hasNext()) {
            key = keys.next();
            if (isFirst) {
                propertyBuilder.append(" SET `").append(key).append("` = ?");
                isFirst = false;
            } else {
                propertyBuilder.append(", `").append(key).append("` = ?");
            }

            paramList.add(needUpdateJsonObject.get(key));
        }

        sqlBuilder.append("UPDATE ").append("`").append(getName()).append("`").append(propertyBuilder).append(" WHERE ").append(JdbcRepositories.keyName).append(" = ?");
        paramList.add(id);
    }

    /**
     * Compares the specified old json object and the new json object, returns diff object for updating.
     *
     * @param oldJsonObject the specified old json object
     * @param jsonObject    the specified new json object
     * @param propertyNames the specified property names
     * @return diff object for updating
     */
    private JSONObject getDiff(final JSONObject oldJsonObject, final JSONObject jsonObject, final String... propertyNames) {
        if (null == oldJsonObject) {
            return jsonObject;
        }

        final JSONObject ret = new JSONObject();
        final Set<String> keys = new HashSet<>();
        if (0 < ArrayUtils.getLength(propertyNames)) {
            keys.addAll(Arrays.asList(propertyNames));
        } else {
            keys.addAll(jsonObject.keySet());
        }

        for (final String key : keys) {
            final Object oldVal = oldJsonObject.get(key);
            final Object val = jsonObject.get(key);
            if (null == val && null == oldVal) {
                ret.put(key, val);
            } else if (!jsonObject.optString(key).equals(oldJsonObject.optString(key))) {
                ret.put(key, val);
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

        final StringBuilder sqlBuilder = new StringBuilder();
        final Connection connection = getConnection();
        try {
            if (Repositories.isSoftDelete()) {
                sqlBuilder.append("UPDATE ").append(getName()).append(" SET `").append(JdbcRepositories.softDeleteFieldName).append("` = 1").
                        append(" WHERE ").append(JdbcRepositories.keyName).append(" = '").append(id).append("'");
            } else {
                sqlBuilder.append("DELETE FROM ").append("`").append(getName()).append("`").append(" WHERE ").append(JdbcRepositories.keyName).append(" = '").append(id).append("'");
            }
            JdbcUtil.executeSql(sqlBuilder.toString(), connection, debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes a record [id=" + id + "] failed", e);

            throw new RepositoryException(e);
        }
    }

    @Override
    public void remove(final Query query) throws RepositoryException {
        final JdbcTransaction currentTransaction = TX.get();
        if (null == currentTransaction) {
            throw new RepositoryException("Invoking remove() outside a transaction");
        }

        final StringBuilder deleteSQLBuilder = new StringBuilder("DELETE FROM ").append("`").append(getName()).append("`");
        final List<Object> paramList = new ArrayList<>();
        final StringBuilder filterSqlBuilder = new StringBuilder();
        buildWhere(filterSqlBuilder, paramList, query.getFilter());
        if (StringUtils.isNotBlank(filterSqlBuilder.toString())) {
            deleteSQLBuilder.append(" WHERE ").append(filterSqlBuilder);
        }

        final Connection connection = getConnection();
        try {
            JdbcUtil.executeSql(deleteSQLBuilder.toString(), paramList, connection, debug);
        } catch (final SQLException e) {
            LOGGER.log(Level.ERROR, "Remove failed", e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject get(final String id) throws RepositoryException {
        JSONObject ret;
        final StringBuilder sqlBuilder = new StringBuilder();
        final Connection connection = getConnection();
        try {
            sqlBuilder.append("SELECT * FROM ").append("`").append(getName()).append("`").append(" WHERE ").append(JdbcRepositories.keyName).append(" = ?");
            if (Repositories.isSoftDelete()) {
                sqlBuilder.append(" AND `").append(JdbcRepositories.softDeleteFieldName).append("` = 0");
            }
            final ArrayList<Object> paramList = new ArrayList<>();
            paramList.add(id);
            ret = JdbcUtil.queryJsonObject(sqlBuilder.toString(), paramList, connection, getName(), debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets a record [id=" + id + "] failed", e);
            throw new RepositoryException(e);
        }
        return ret;
    }

    @Override
    public Map<String, JSONObject> get(final Iterable<String> ids) throws RepositoryException {
        final Map<String, JSONObject> ret = new HashMap<>();
        for (final String id : ids) {
            final JSONObject jsonObject = get(id);
            ret.put(jsonObject.optString(JdbcRepositories.keyName), jsonObject);
        }
        return ret;
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

        final StringBuilder sqlBuilder = new StringBuilder();
        final Connection connection = getConnection();
        final List<Object> paramList = new ArrayList<>();
        try {
            final Map<String, Object> paginationCnt = buildSQLCount(currentPageNum, pageSize, pageCount, query, sqlBuilder, paramList);
            final JSONObject pagination = new JSONObject();
            final int pageCnt = (Integer) paginationCnt.get(Pagination.PAGINATION_PAGE_COUNT);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCnt);
            pagination.put(Pagination.PAGINATION_RECORD_COUNT, paginationCnt.get(Pagination.PAGINATION_RECORD_COUNT));
            ret.put(Pagination.PAGINATION, pagination);
            if (0 == pageCnt) {
                ret.put(Keys.RESULTS, (Object) new ArrayList<>());
                return ret;
            }

            final List<JSONObject> list = JdbcUtil.queryListJson(sqlBuilder.toString(), paramList, connection, getName(), query.isDebug());
            ret.put(Keys.RESULTS, (Object) list);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Query failed", e);
            throw new RepositoryException(e);
        }
        return ret;
    }

    @Override
    public List<JSONObject> select(final String statement, final Object... params) throws RepositoryException {
        List<JSONObject> ret;
        final Connection connection = getConnection();
        try {
            if (ArrayUtils.isEmpty(params)) {
                ret = JdbcUtil.queryListJson(statement, Collections.emptyList(), connection, getName(), debug);
            } else {
                ret = JdbcUtil.queryListJson(statement, Arrays.asList(params), connection, getName(), debug);
            }
            return ret;
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
    private Map<String, Object> buildSQLCount(final int currentPageNum, final int pageSize, final int pageCount, final Query query, final StringBuilder sqlBuilder, final List<Object> paramList) throws RepositoryException {
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
            final StringBuilder countBuilder = new StringBuilder("SELECT COUNT(" + JdbcRepositories.keyName + ") FROM ").append("`").append(getName()).append("`");
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
        sqlBuilder.append(JdbcFactory.getInstance().queryPage(start, end, selectBuilder.toString(), whereBuilder.toString(), orderByBuilder.toString(), getName()));
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
        final Filter whereFilter;
        if (Repositories.isSoftDelete()) {
            if (null != filter) {
                whereFilter = CompositeFilterOperator.and(filter, new PropertyFilter(JdbcRepositories.softDeleteFieldName, FilterOperator.EQUAL, 0));
            } else {
                whereFilter = new PropertyFilter(JdbcRepositories.softDeleteFieldName, FilterOperator.EQUAL, 0);
            }
        } else {
            whereFilter = filter;
        }

        if (null == whereFilter) {
            return;
        }

        if (whereFilter instanceof PropertyFilter) {
            processPropertyFilter(whereBuilder, paramList, (PropertyFilter) whereFilter);
        } else { // CompositeFiler
            processCompositeFilter(whereBuilder, paramList, (CompositeFilter) whereFilter);
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
        final Connection connection = getConnection();
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(JdbcFactory.getInstance().getRandomlySql(getName(), fetchSize));
        try {
            return JdbcUtil.queryListJson(sqlBuilder.toString(), new ArrayList<>(), connection, getName(), debug);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Get list randomly failed", e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        final StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(" + JdbcRepositories.keyName + ") FROM ").append("`").append(getName()).append("`");
        return count(sqlBuilder, new ArrayList<>());
    }

    @Override
    public long count(final Query query) throws RepositoryException {
        final StringBuilder countSqlBuilder = new StringBuilder("SELECT COUNT(" + JdbcRepositories.keyName + ") FROM ").append("`").append(getName()).append("`");
        final List<Object> paramList = new ArrayList<>();
        final StringBuilder filterSqlBuilder = new StringBuilder();
        buildWhere(filterSqlBuilder, paramList, query.getFilter());
        if (StringUtils.isNotBlank(filterSqlBuilder.toString())) {
            countSqlBuilder.append(" WHERE ").append(filterSqlBuilder);
        }
        return (int) count(countSqlBuilder, paramList);
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
                ? Latkes.getLocalProperty("jdbc.tablePrefix") + "_" : "";
        return tableNamePrefix + name;
    }

    @Override
    public Transaction beginTransaction() {
        JdbcTransaction ret = TX.get();
        if (null != ret && ret.isActive()) {
            return TX.get(); // Using 'the current transaction'
        }

        try {
            ret = new JdbcTransaction();
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
    private void processPropertyFilter(final StringBuilder whereBuilder, final List<Object> paramList, final PropertyFilter propertyFilter) throws RepositoryException {
        String filterOperator;
        final FilterOperator operator = propertyFilter.getOperator();
        switch (operator) {
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
            case NOT_IN:
                filterOperator = "NOT IN";
                break;
            case LIKE:
                filterOperator = "LIKE";
                break;
            case NOT_LIKE:
                filterOperator = "NOT LIKE";
                break;
            default:
                throw new RepositoryException("Unsupported filter operator [" + operator + "]");
        }

        if (FilterOperator.IN != operator && FilterOperator.NOT_IN != operator) {
            whereBuilder.append(propertyFilter.getKey()).append(" ").append(filterOperator).append(" ?");
            paramList.add(propertyFilter.getValue());
        } else {
            final Collection<Object> objects = (Collection<Object>) propertyFilter.getValue();
            if (null != objects && !objects.isEmpty()) {
                whereBuilder.append(propertyFilter.getKey());
                if (FilterOperator.IN == operator) {
                    whereBuilder.append(" IN ");
                } else {
                    whereBuilder.append(" NOT IN ");
                }

                boolean isSubFist = true;
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
            } else {
                if (FilterOperator.IN == operator) {
                    // IN () => 1!=1
                    whereBuilder.append("1 != 1");
                } else {
                    // NOT IN () => 1=1
                    whereBuilder.append("1 = 1");
                }
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
    private void processCompositeFilter(final StringBuilder whereBuilder, final List<Object> paramList, final CompositeFilter compositeFilter) throws RepositoryException {
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
}
