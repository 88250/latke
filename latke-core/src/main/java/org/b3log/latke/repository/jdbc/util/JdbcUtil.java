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
package org.b3log.latke.repository.jdbc.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JDBC utilities.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.2.7, Jun 5, 2018
 */
public final class JdbcUtil {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcUtil.class);

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
        final boolean isSuccess = !statement.execute(sql);
        statement.close();

        return isSuccess;
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
        final boolean isSuccess = preparedStatement.execute();
        preparedStatement.close();

        return isSuccess;
    }

    /**
     * queryJsonObject.
     *
     * @param sql        sql
     * @param paramList  paramList
     * @param connection connection
     * @param tableName  tableName
     * @param isDebug    the specified debug flag
     * @return JSONObject only one record.
     * @throws SQLException        SQLException
     * @throws JSONException       JSONException
     * @throws RepositoryException repositoryException
     */
    public static JSONObject queryJsonObject(final String sql, final List<Object> paramList, final Connection connection,
                                             final String tableName, final boolean isDebug) throws SQLException, JSONException, RepositoryException {
        return queryJson(sql, paramList, connection, true, tableName, isDebug);
    }

    /**
     * queryJsonArray.
     *
     * @param sql        sql
     * @param paramList  paramList
     * @param connection connection
     * @param tableName  tableName
     * @param isDebug    the specified debug flag
     * @return JSONArray
     * @throws SQLException        SQLException
     * @throws JSONException       JSONException
     * @throws RepositoryException repositoryException
     */
    public static JSONArray queryJsonArray(final String sql, final List<Object> paramList, final Connection connection,
                                           final String tableName, final boolean isDebug) throws SQLException, JSONException, RepositoryException {
        final JSONObject jsonObject = queryJson(sql, paramList, connection, false, tableName, isDebug);

        return jsonObject.getJSONArray(Keys.RESULTS);
    }

    /**
     * @param sql        sql
     * @param paramList  paramList
     * @param connection connection
     * @param ifOnlyOne  ifOnlyOne to determine return object or array.
     * @param tableName  tableName
     * @param isDebug    the specified debug flag
     * @return JSONObject
     * @throws SQLException        SQLException
     * @throws JSONException       JSONException
     * @throws RepositoryException respsitoryException
     */
    private static JSONObject queryJson(final String sql, final List<Object> paramList, final Connection connection,
                                        final boolean ifOnlyOne, final String tableName, final boolean isDebug) throws SQLException, JSONException, RepositoryException {
        if (isDebug || LOGGER.isTraceEnabled()) {
            LOGGER.log(Level.INFO, "Executing SQL [" + sql + "]");
        }

        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 1; i <= paramList.size(); i++) {
            preparedStatement.setObject(i, paramList.get(i - 1));
        }

        final ResultSet resultSet = preparedStatement.executeQuery();
        final JSONObject jsonObject = resultSetToJsonObject(resultSet, ifOnlyOne, tableName);

        resultSet.close();
        preparedStatement.close();

        return jsonObject;
    }

    /**
     * resultSetToJsonObject.
     *
     * @param resultSet resultSet
     * @param ifOnlyOne ifOnlyOne
     * @param tableName tableName
     * @return JSONObject
     * @throws SQLException        SQLException
     * @throws JSONException       JSONException
     * @throws RepositoryException RepositoryException
     */
    private static JSONObject resultSetToJsonObject(final ResultSet resultSet, final boolean ifOnlyOne, final String tableName)
            throws SQLException, JSONException, RepositoryException {
        final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        final List<FieldDefinition> definitionList = JdbcRepositories.getKeys(tableName);
        if (null == definitionList) {
            LOGGER.log(Level.ERROR, "resultSetToJsonObject: null definitionList finded for table  {0}", tableName);
            throw new RepositoryException("resultSetToJsonObject: null definitionList finded for table  " + tableName);
        }

        final Map<String, FieldDefinition> dMap = new HashMap<>();
        for (FieldDefinition fieldDefinition : definitionList) {
            if (Latkes.RuntimeDatabase.H2 == Latkes.getRuntimeDatabase() || Latkes.RuntimeDatabase.ORACLE == Latkes.getRuntimeDatabase()) {
                dMap.put(fieldDefinition.getName().toUpperCase(), fieldDefinition);
            } else {
                dMap.put(fieldDefinition.getName(), fieldDefinition);
            }
        }

        final int numColumns = resultSetMetaData.getColumnCount();

        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        String columnName;

        while (resultSet.next()) {
            jsonObject = new JSONObject();

            for (int i = 1; i < numColumns + 1; i++) {
                columnName = resultSetMetaData.getColumnName(i);

                final FieldDefinition definition = dMap.get(columnName);

                if (null == definition) { // COUNT(OID)
                    jsonObject.put(columnName, resultSet.getObject(columnName));
                } else if ("boolean".equals(definition.getType())) {
                    jsonObject.put(definition.getName(), resultSet.getBoolean(columnName));
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

                        jsonObject.put(definition.getName(), str);
                    } else {
                        jsonObject.put(definition.getName(), v);
                    }
                }
            }

            if (Latkes.RuntimeDatabase.ORACLE == Latkes.getRuntimeDatabase()) {
                jsonObject.remove("R__");
                fromOracleClobEmpty(jsonObject);
            }

            jsonArray.put(jsonObject);
        }

        if (ifOnlyOne) {
            if (jsonArray.length() > 0) {
                jsonObject = jsonArray.getJSONObject(0);
                return jsonObject;
            }

            return null;
        }

        jsonObject = new JSONObject();
        jsonObject.put(Keys.RESULTS, jsonArray);

        return jsonObject;
    }

    /**
     * Process Oracle CLOB empty string.
     *
     * @param jsonObject the specified JSON object
     */
    public static void fromOracleClobEmpty(final JSONObject jsonObject) {
        final Iterator<String> keys = jsonObject.keys();
        try {
            while (keys.hasNext()) {
                final String name = keys.next();
                final Object val = jsonObject.get(name);
                if (val instanceof String) {
                    final String valStr = (String) val;
                    if (StringUtils.equals(valStr, JdbcRepository.ORA_EMPTY_STR)) {
                        jsonObject.put(name, "");
                    }
                }
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.ERROR, "Process oracle clob empty failed", e);
        }
    }

    /**
     * Private constructor.
     */
    private JdbcUtil() {
    }
}
