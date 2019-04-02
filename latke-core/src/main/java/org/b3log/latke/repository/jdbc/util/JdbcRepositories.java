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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.jdbc.JdbcFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Jdbc repository utilities.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.3, Mar 2, 2019
 */
public final class JdbcRepositories {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcRepositories.class);

    /**
     * "repositories".
     */
    private static final String REPOSITORIES = "repositories";

    /**
     * "description".
     */
    private static final String DESCRIPTION = "description";

    /**
     * "name".
     */
    private static final String NAME = "name";

    /**
     * "charset".
     */
    private static final String CHARSET = "charset";

    /**
     * "collate".
     */
    private static final String COLLATE = "collate";

    /**
     * "keys".
     */
    private static final String KEYS = "keys";

    /**
     * "type".
     */
    private static final String TYPE = "type";

    /**
     * "nullable".
     */
    private static final String NULLABLE = "nullable";

    /**
     * "length".
     */
    private static final String LENGTH = "length";

    /**
     * "iskey".
     */
    private static final String ISKEY = "iskey";

    /**
     * The default primary key name.
     */
    private static String defaultKeyName = "oId";

    /**
     * Stores all repository definitions.
     */
    private static List<RepositoryDefinition> repositoryDefinitions = null;

    /**
     * Sets the default key name.
     *
     * @param keyName the specified key name
     */
    public static void setDefaultKeyName(final String keyName) {
        defaultKeyName = keyName;
    }

    /**
     * Gets the default key name.
     *
     * @return default key name
     */
    public static String getDefaultKeyName() {
        return defaultKeyName;
    }

    /**
     * Gets keys of the repository specified by the given repository name.
     *
     * @param repositoryName the given repository name
     * @return keys
     */
    public static List<FieldDefinition> getKeys(final String repositoryName) {
        final List<RepositoryDefinition> repositoryDefs = getRepositoryDefinitions();
        for (final RepositoryDefinition repositoryDefinition : repositoryDefs) {
            if (StringUtils.equals(repositoryName, repositoryDefinition.getName())) {
                return repositoryDefinition.getKeys();
            }
        }

        return null;
    }

    /**
     * Gets the repository definitions,lazy load.
     *
     * @return repository definitions
     */
    public static List<RepositoryDefinition> getRepositoryDefinitions() {
        if (null == repositoryDefinitions) {
            try {
                initRepositoryDefinitions();
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Init repository definitions failed", e);
            }
        }

        return repositoryDefinitions;
    }

    /**
     * Initializes the repository definitions.
     *
     * @throws JSONException JSONException
     */
    private static void initRepositoryDefinitions() throws JSONException {
        final JSONObject jsonObject = Repositories.getRepositoriesDescription();
        if (null == jsonObject) {
            LOGGER.warn("Loads repository description [repository.json] failed");

            return;
        }

        repositoryDefinitions = new ArrayList<>();
        final JSONArray repositoritArray = jsonObject.getJSONArray(REPOSITORIES);
        JSONObject repositoryObject;
        JSONObject keyObject;
        for (int i = 0; i < repositoritArray.length(); i++) {
            repositoryObject = repositoritArray.getJSONObject(i);

            final RepositoryDefinition repositoryDefinition = new RepositoryDefinition();
            repositoryDefinitions.add(repositoryDefinition);
            repositoryDefinition.setName(repositoryObject.getString(NAME));
            repositoryDefinition.setDescription(repositoryObject.optString(DESCRIPTION));
            final List<FieldDefinition> keys = new ArrayList<>();
            repositoryDefinition.setKeys(keys);
            final JSONArray keysJsonArray = repositoryObject.getJSONArray(KEYS);
            FieldDefinition definition;
            for (int j = 0; j < keysJsonArray.length(); j++) {
                keyObject = keysJsonArray.getJSONObject(j);
                definition = fillFieldDefinitionData(keyObject);
                keys.add(definition);
            }
            repositoryDefinition.setCharset(repositoryObject.optString(CHARSET));
            repositoryDefinition.setCollate(repositoryObject.optString(COLLATE));
        }
    }

    /**
     * fillFieldDefinitionData.
     *
     * @param fieldDefinitionObject josn model
     * @return {@link FieldDefinition}
     * @throws JSONException JSONException
     */
    private static FieldDefinition fillFieldDefinitionData(final JSONObject fieldDefinitionObject) throws JSONException {
        final FieldDefinition ret = new FieldDefinition();
        ret.setName(fieldDefinitionObject.getString(NAME));
        ret.setDescription(fieldDefinitionObject.optString(DESCRIPTION));
        ret.setType(fieldDefinitionObject.getString(TYPE));
        ret.setNullable(fieldDefinitionObject.optBoolean(NULLABLE));
        ret.setLength(fieldDefinitionObject.optInt(LENGTH));
        ret.setIsKey(fieldDefinitionObject.optBoolean(ISKEY));
        if (defaultKeyName.equals(ret.getName())) {
            ret.setIsKey(true);
        }

        return ret;
    }

    /**
     * createTableResult model for view to show.
     */
    public static class CreateTableResult {

        /**
         * table name.
         */
        private String name;

        /**
         * isCreate success.
         */
        private boolean isSuccess;

        /**
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name tableName
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * @return isSuccess
         */
        public boolean isSuccess() {
            return isSuccess;
        }

        /**
         * @param isSuccess isSuccess
         */
        public void setSuccess(final boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        /**
         * constructor.
         *
         * @param name      table
         * @param isSuccess isSuccess
         */
        public CreateTableResult(final String name, final boolean isSuccess) {
            super();
            this.name = name;
            this.isSuccess = isSuccess;
        }
    }

    /**
     * Checks whether a table specified by the given table name exists.
     *
     * @param tableName the given table name
     * @return {@code true} if it exists, returns {@code false} otherwise
     */
    public static boolean existTable(final String tableName) {
        return JdbcFactory.getInstance().existTable(tableName);
    }

    /**
     * Initializes all tables from repository.json.
     *
     * @return List<CreateTableResult>
     */
    public static List<CreateTableResult> initAllTables() {
        final List<CreateTableResult> ret = new ArrayList<>();
        final List<RepositoryDefinition> repositoryDefs = getRepositoryDefinitions();
        boolean isSuccess = false;
        for (final RepositoryDefinition repositoryDef : repositoryDefs) {
            try {
                isSuccess = JdbcFactory.getInstance().createTable(repositoryDef);
            } catch (final SQLException e) {
                LOGGER.log(Level.ERROR, "Creates table [" + repositoryDef.getName() + "] error", e);
            }

            ret.add(new CreateTableResult(repositoryDef.getName(), isSuccess));
        }

        return ret;
    }

    /**
     * Generates repository.json from databases.
     *
     * @param tablePrefix the specified table prefix, for example "symphony_". An empty string {@code ""} means no table prefix
     * @param tableNames  the specified table names for generation
     * @param destPath    the specified path of repository.json file to generate
     */
    public static void initRepositoryJSON(final String tablePrefix, final Set<String> tableNames, final String destPath) {
        Connection connection;
        FileWriter writer = null;

        try {
            final File file = new File(destPath);
            if (file.isDirectory()) {
                LOGGER.log(Level.ERROR, "Can't generate repository definition file caused by the specified destination path [" + destPath + "] is a dir");

                return;
            }

            connection = Connections.getConnection();

            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            final ResultSet resultSet = databaseMetaData.getTables(null, "%", "%", new String[]{"TABLE"});

            final JSONObject repositoryJSON = new JSONObject();
            final JSONArray repositories = new JSONArray();

            repositoryJSON.put("repositories", repositories);

            while (resultSet.next()) {
                final String fullTableName = resultSet.getString("TABLE_NAME");
                final String tableName = StringUtils.substringAfter(fullTableName, tablePrefix);
                if (!tableNames.contains(tableName)) {
                    continue;
                }

                final JSONObject repository = new JSONObject();
                repositories.put(repository);
                repository.put("name", tableName);
                String remarks = resultSet.getString("REMARKS");
                if (StringUtils.isNotBlank(remarks)) {
                    repository.put("description", remarks);
                }
                final JSONArray keys = new JSONArray();
                repository.put("keys", keys);

                final ResultSet rs = databaseMetaData.getColumns(null, "%", fullTableName, "%");
                while (rs.next()) {
                    final String columnName = rs.getString("COLUMN_NAME");
                    remarks = rs.getString("REMARKS");
                    final int dataType = rs.getInt("DATA_TYPE");
                    final int length = rs.getInt("COLUMN_SIZE");
                    final int nullable = rs.getInt("NULLABLE");

                    final JSONObject key = new JSONObject();
                    keys.put(key);
                    key.put("name", columnName);
                    if (StringUtils.isNotBlank(remarks)) {
                        key.put("description", remarks);
                    }
                    if (0 != nullable) {
                        key.put("nullable", true);
                    }

                    switch (dataType) {
                        case Types.CHAR:
                        case Types.LONGNVARCHAR:
                        case Types.LONGVARCHAR:
                        case Types.NCHAR:
                        case Types.NVARCHAR:
                        case Types.VARCHAR:
                            key.put("type", "String");

                            break;
                        case Types.BIGINT:
                            key.put("type", "long");

                            break;
                        case Types.INTEGER:
                        case Types.SMALLINT:
                        case Types.TINYINT:
                            key.put("type", "int");

                            break;
                        case Types.DATE:
                            key.put("type", "Date");

                            break;
                        case Types.TIME:
                        case Types.TIMESTAMP:
                            key.put("type", "Datetime");

                            break;
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                            key.put("type", "Decimal");
                            key.put("precision", rs.getInt("DECIMAL_DIGITS"));

                            break;
                        case Types.BIT:
                            key.put("type", "Bit");

                            break;
                        case Types.CLOB:
                            key.put("type", "Clob");

                            break;
                        case Types.BLOB:
                            key.put("type", "Blob");

                            break;
                        case Types.DOUBLE:
                            key.put("type", "double");

                            break;
                        default:
                            throw new IllegalStateException("Unsupported type [" + dataType + ']');
                    }

                    key.put("length", length);
                }
            }

            FileUtils.deleteQuietly(file);
            writer = new FileWriter(file);
            final String content = repositoryJSON.toString(Integer.valueOf("2"));
            IOUtils.write(content, writer);

            LOGGER.log(Level.INFO, "Generated repository definition file [" + destPath + "]");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Init repository.json failed", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    /**
     * Sets the repository definitions.
     *
     * @param repositoryDefinitions repositoryDefinitions
     */
    public static void setRepositoryDefinitions(final List<RepositoryDefinition> repositoryDefinitions) {
        JdbcRepositories.repositoryDefinitions = repositoryDefinitions;
    }

    /**
     * Private constructor.
     */
    private JdbcRepositories() {
    }
}
