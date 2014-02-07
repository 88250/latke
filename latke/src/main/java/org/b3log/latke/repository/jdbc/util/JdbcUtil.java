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
package org.b3log.latke.repository.jdbc.util;


import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeDatabase;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * JDBC utilities.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Feb 7, 2014
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
    public static boolean executeSql(final String sql, final Connection connection) throws SQLException {
        LOGGER.log(Level.TRACE, "executeSql: {0}", sql);

        final Statement statement = connection.createStatement();
        final boolean isSuccess = !statement.execute(sql);

        statement.close();
        return isSuccess;
    }

    /**
     * executeSql.
     * 
     * @param sql sql
     * @param paramList paramList
     * @param connection connection
     * @return is success
     * @throws SQLException SQLException
     */
    public static boolean executeSql(final String sql, final List<Object> paramList, final Connection connection) throws SQLException {
        LOGGER.log(Level.TRACE, "Execute SQL [{0}]", sql);

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
    public static JSONObject queryJsonObject(final String sql, final List<Object> paramList, final Connection connection,
        final String tableName) throws SQLException, JSONException, RepositoryException {

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
    public static JSONArray queryJsonArray(final String sql, final List<Object> paramList, final Connection connection,
        final String tableName) throws SQLException, JSONException, RepositoryException {
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
    private static JSONObject queryJson(final String sql, final List<Object> paramList, final Connection connection,
        final boolean ifOnlyOne, final String tableName) throws SQLException, JSONException, RepositoryException {
        LOGGER.log(Level.TRACE, "Query SQL [{0}]", sql);

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
     * 
     * @return JSONObject
     * @throws SQLException SQLException
     * @throws JSONException JSONException
     * @throws RepositoryException RepositoryException
     */
    private static JSONObject resultSetToJsonObject(final ResultSet resultSet, final boolean ifOnlyOne, final String tableName)
        throws SQLException, JSONException, RepositoryException {
        final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        final List<FieldDefinition> definitioList = JdbcRepositories.getRepositoriesMap().get(tableName);

        if (definitioList == null) {
            LOGGER.log(Level.ERROR, "resultSetToJsonObject: null definitioList finded for table  {0}", tableName);
            throw new RepositoryException("resultSetToJsonObject: null definitioList finded for table  " + tableName);
        }

        final Map<String, FieldDefinition> dMap = new HashMap<String, FieldDefinition>();

        for (FieldDefinition fieldDefinition : definitioList) {
            if (RuntimeDatabase.H2 == Latkes.getRuntimeDatabase()) {
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

                if (definition == null) { // COUNT(OID)
                    jsonObject.put(columnName, resultSet.getObject(columnName));
                } else {
                    if ("boolean".equals(definition.getType())) {
                        jsonObject.put(definition.getName(), resultSet.getBoolean(columnName));
                    } else {
                        final Object v = resultSet.getObject(columnName);
                        
                        while (true) {
                            if (RuntimeDatabase.H2 != Latkes.getRuntimeDatabase()) {
                                jsonObject.put(definition.getName(), v);

                                break;
                            }

                            // H2
                            if ("String".equals(definition.getType()) && v instanceof Clob) { // H2 CLOB
                                final Clob clob = (Clob) v;

                                String str = null;

                                try {
                                    str = IOUtils.toString(clob.getCharacterStream());
                                } catch (final IOException e) {
                                    LOGGER.log(Level.ERROR,
                                        "Cant not read column[name=" + columnName + "] in table[name=" + tableName + "] on H2", e);
                                } finally {
                                    clob.free();
                                }

                                jsonObject.put(definition.getName(), str);

                                break;
                            }

                            // H2 other types
                            jsonObject.put(definition.getName(), v);

                            break;
                        }
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
    private JdbcUtil() {}
}
