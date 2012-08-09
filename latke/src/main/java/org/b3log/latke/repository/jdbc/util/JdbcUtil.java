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
package org.b3log.latke.repository.jdbc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * jdbcUtil.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public final class JdbcUtil {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcUtil.class.getName());

    /**
     * executeSql.
     * 
     * @param sql sql
     * @param connection connection
     * @return ifsuccess
     * @throws SQLException SQLException
     */
    public static boolean executeSql(final String sql,
            final Connection connection) throws SQLException {

        LOGGER.info("executeSql:" + sql);
        final Statement statement = connection.createStatement();
        final boolean isSuccess = statement.execute(sql);
        statement.close();

        return isSuccess;
    }

    /**
     * executeSql.
     * 
     * @param sql sql
     * @param paramList paramList
     * @param connection connection
     * @return issuccess
     * @throws SQLException SQLException
     */
    public static boolean executeSql(final String sql,
            final List<Object> paramList, final Connection connection)
            throws SQLException {

        LOGGER.info("executeSql:" + sql);
        
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
     * @param sql sql
     * @param paramList paramList
     * @param connection connection
     * @param tableName tableName
     * 
     * @return JSONObject only one record.
     * @throws SQLException SQLException
     * @throws JSONException JSONException
     * @throws RepositoryException repositoryException
     */
    public static JSONObject queryJsonObject(final String sql,
            final List<Object> paramList, final Connection connection,
            final String tableName) throws SQLException, JSONException,
            RepositoryException {

        return queryJson(sql, paramList, connection, true, tableName);

    }

    /**
     * queryJsonArray.
     * 
     * @param sql sql
     * @param paramList paramList
     * @param connection connection
     * @param tableName tableName
     * 
     * @return JSONArray
     * @throws SQLException SQLException
     * @throws JSONException JSONException
     * @throws RepositoryException repositoryException
     */
    public static JSONArray queryJsonArray(final String sql,
            final List<Object> paramList, final Connection connection,
            final String tableName) throws SQLException, JSONException,
            RepositoryException {
        final JSONObject jsonObject = queryJson(sql, paramList, connection, false, tableName);
        return jsonObject.getJSONArray(Keys.RESULTS);

    }

    /**
     * @param sql sql
     * @param paramList paramList
     * @param connection connection
     * @param ifOnlyOne ifOnlyOne to determine return object or array.
     * @param tableName tableName
     * 
     * @return JSONObject
     * @throws SQLException SQLException
     * @throws JSONException JSONException
     * @throws RepositoryException respsitoryException
     */
    private static JSONObject queryJson(final String sql,
            final List<Object> paramList, final Connection connection,
            final boolean ifOnlyOne, final String tableName)
            throws SQLException, JSONException, RepositoryException {

        LOGGER.info("querySql:" + sql);

        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        for (int i = 1; i <= paramList.size(); i++) {
            preparedStatement.setObject(i, paramList.get(i - 1));
        }

        final ResultSet resultSet = preparedStatement.executeQuery();

        final JSONObject jsonObject = resultSetToJsonObject(resultSet, ifOnlyOne, tableName);
        preparedStatement.close();
        return jsonObject;

    }

    /**
     * resultSetToJsonObject.
     * 
     * @param resultSet resultSet
     * @param ifOnlyOne ifOnlyOne
     * @param tableName tableName
     * 
     * @return JSONObject
     * @throws SQLException SQLException
     * @throws JSONException JSONException
     * @throws RepositoryException RepositoryException
     */
    private static JSONObject resultSetToJsonObject(final ResultSet resultSet,
            final boolean ifOnlyOne, final String tableName)
            throws SQLException, JSONException, RepositoryException {
        final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        final List<FieldDefinition> definitioList = JdbcRepositories.getRepositoriesMap().get(tableName);

        if (definitioList == null) {
            LOGGER.log(Level.SEVERE, "resultSetToJsonObject: null definitioList finded for table  {0}", tableName);
            throw new RepositoryException("resultSetToJsonObject: null definitioList finded for table  " + tableName);
        }

        final Map<String, FieldDefinition> dMap = new HashMap<String, FieldDefinition>();
        for (FieldDefinition fieldDefinition : definitioList) {
            dMap.put(fieldDefinition.getName(), fieldDefinition);
        }

        final int numColumns = resultSetMetaData.getColumnCount();

        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        String columnName = null;
        while (resultSet.next()) {
            jsonObject = new JSONObject();

            for (int i = 1; i < numColumns + 1; i++) {
                columnName = resultSetMetaData.getColumnName(i);

                final FieldDefinition definition = dMap.get(columnName);
                if (definition == null) {
                    //                    throw new RepositoryException(
                    //                            "resultSetToJsonObject: null columnName["
                    //                                    + columnName + "] finded in table  "
                    //                                    + tableName);
                    jsonObject.put(columnName, resultSet.getObject(columnName));
                } else {
                    if ("boolean".equals(definition.getType())) {
                        jsonObject.put(columnName, resultSet.getBoolean(columnName));
                    } else {
                        jsonObject.put(columnName, resultSet.getObject(columnName));
                    }
                }
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
     * Private constructor.
     */
    private JdbcUtil() {
    }
}
