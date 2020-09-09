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
package org.b3log.latke.repository.jdbc.util;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC utilities.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Jun 20, 2020
 */
public final class JdbcUtil {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(JdbcUtil.class);

    /**
     * Executes the specified SQL with the specified connection.
     *
     * @param sql        the specified SQL
     * @param connection connection the specified connection
     * @param isDebug    the specified debug flag
     * @return {@code true} if success, returns {@code false} otherwise
     * @throws SQLException SQLException
     */
    public static boolean executeSql(final String sql, final Connection connection, final boolean isDebug) throws SQLException {
        if (isDebug || LOGGER.isTraceEnabled()) {
            LOGGER.log(Level.INFO, "Executing SQL [" + sql + "]");
        }

        final Statement statement = connection.createStatement();
        final boolean ret = !statement.execute(sql);
        statement.close();
        return ret;
    }

    /**
     * Executes the specified SQL with the specified params and connection...
     *
     * @param sql        the specified SQL
     * @param paramList  the specified params
     * @param connection the specified connection
     * @param isDebug    the specified debug flag
     * @return {@code true} if success, returns {@code false} otherwise
     * @throws SQLException SQLException
     */
    public static boolean executeSql(final String sql, final List<Object> paramList, final Connection connection, final boolean isDebug) throws SQLException {
        if (isDebug || LOGGER.isTraceEnabled()) {
            LOGGER.log(Level.INFO, "Executing SQL [" + sql + "]");
        }

        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 1; i <= paramList.size(); i++) {
            preparedStatement.setObject(i, paramList.get(i - 1));
        }
        final boolean ret = preparedStatement.execute();
        preparedStatement.close();
        return ret;
    }

    /**
     * Queries a JSON object.
     *
     * @param sql        sql
     * @param paramList  paramList
     * @param connection connection
     * @param tableName  tableName
     * @param isDebug    the specified debug flag
     * @return JSONObject only one record.
     * @throws Exception Exception
     */
    public static JSONObject queryJsonObject(final String sql, final List<Object> paramList, final Connection connection, final String tableName, final boolean isDebug) throws Exception {
        return queryJson(sql, paramList, connection, true, tableName, isDebug);
    }

    /**
     * Queries a list of JSON objects.
     *
     * @param sql        sql
     * @param paramList  paramList
     * @param connection connection
     * @param tableName  tableName
     * @param isDebug    the specified debug flag
     * @return a list of JSON object
     * @throws Exception Exception
     */
    public static List<JSONObject> queryListJson(final String sql, final List<Object> paramList, final Connection connection, final String tableName, final boolean isDebug) throws Exception {
        final JSONObject jsonObject = queryJson(sql, paramList, connection, false, tableName, isDebug);
        return (List<JSONObject>) jsonObject.opt(Keys.RESULTS);
    }

    /**
     * @param sql        sql
     * @param paramList  paramList
     * @param connection connection
     * @param ifOnlyOne  ifOnlyOne to determine return object or array.
     * @param tableName  tableName
     * @param isDebug    the specified debug flag
     * @return JSONObject
     * @throws Exception Exception
     */
    private static JSONObject queryJson(final String sql, final List<Object> paramList, final Connection connection, final boolean ifOnlyOne, final String tableName, final boolean isDebug) throws Exception {
        if (isDebug || LOGGER.isTraceEnabled()) {
            LOGGER.log(Level.INFO, "Executing SQL [" + sql + "]");
        }

        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 1; i <= paramList.size(); i++) {
            preparedStatement.setObject(i, paramList.get(i - 1));
        }
        final ResultSet resultSet = preparedStatement.executeQuery();
        final JSONObject ret = resultSetToJsonObject(resultSet, ifOnlyOne, tableName);
        resultSet.close();
        preparedStatement.close();
        return ret;
    }

    /**
     * Converts the specified query result set to JSON object.
     *
     * @param resultSet resultSet the specified query result set
     * @param ifOnlyOne ifOnlyOne
     * @param tableName tableName
     * @return JSONObject
     */
    private static JSONObject resultSetToJsonObject(final ResultSet resultSet, final boolean ifOnlyOne, final String tableName) throws Exception {
        final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        final List<FieldDefinition> definitionList = JdbcRepositories.getKeys(tableName);
        if (null == definitionList) {
            throw new RepositoryException("Null definition list for table [" + tableName + "]");
        }

        final Map<String, FieldDefinition> dMap = new HashMap<>();
        for (final FieldDefinition fieldDefinition : definitionList) {
            if (Latkes.RuntimeDatabase.H2 == Latkes.getRuntimeDatabase()) {
                dMap.put(fieldDefinition.getName().toUpperCase(), fieldDefinition);
            } else {
                dMap.put(fieldDefinition.getName(), fieldDefinition);
            }
        }

        final int numColumns = resultSetMetaData.getColumnCount();
        final List<JSONObject> list = new ArrayList<>();
        JSONObject ret;
        String columnName;
        while (resultSet.next()) {
            ret = new JSONObject();
            for (int i = 1; i < numColumns + 1; i++) {
                columnName = resultSetMetaData.getColumnLabel(i);
                final FieldDefinition definition = dMap.get(columnName);
                if (null == definition) { // COUNT(OID)
                    ret.put(columnName, resultSet.getObject(columnName));
                } else if ("boolean".equals(definition.getType())) {
                    ret.put(definition.getName(), resultSet.getBoolean(columnName));
                } else {
                    final Object v = resultSet.getObject(columnName);
                    if (v instanceof Clob) {
                        final Clob clob = (Clob) v;
                        String str = null;
                        try {
                            str = IOUtils.toString(clob.getCharacterStream());
                        } catch (final IOException e) {
                            LOGGER.log(Level.ERROR,
                                    "Cant not read column[name=" + columnName + "] in table[name=" + tableName + "] on H2", e);
                        } finally {
                            try {
                                clob.free();
                            } catch (final Exception e) { // Some drivers dose not implement free(), for example, jtds
                                LOGGER.log(Level.ERROR, "clob.free error", e);
                            }
                        }

                        ret.put(definition.getName(), str);
                    } else {
                        ret.put(definition.getName(), v);
                    }
                }
            }

            list.add(ret);
        }

        if (ifOnlyOne) {
            if (list.isEmpty()) {
                return null;
            }
            return list.get(0);
        }

        ret = new JSONObject();
        ret.put(Keys.RESULTS, (Object) list);
        return ret;
    }

    /**
     * Private constructor.
     */
    private JdbcUtil() {
    }
}
