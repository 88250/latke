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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.jdbc.JdbcFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JdbcRepositories utilities.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Dec 20, 2011
 */
public final class JdbcRepositories {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcRepositories.class.getName());
    /**
     * the String jsonType to JdbcType.
     */
    //    @SuppressWarnings("serial")
    //    private static final Map<String, Integer> JSONTYPETOJDBCTYPEMAP =
    //            new HashMap<String, Integer>() {
    //                {
    //
    //                    put("int", Types.INTEGER);
    //                    put("long", Types.BIGINT);
    //                    put("String", Types.VARCHAR);
    //                    put("boolean", Types.BOOLEAN);
    //                    put("double", Types.DOUBLE);
    //
    //                }
    //            };
    /**
     * /** to json "repositories".
     */
    private static final String REPOSITORIES = "repositories";
    /**
     * /** to json "name".
     */
    private static final String NAME = "name";
    /**
     * /** to json "keys".
     */
    private static final String KEYS = "keys";
    /**
     * /** to json "type".
     */
    private static final String TYPE = "type";
    /**
     * /** to json "nullable".
     */
    private static final String NULLABLE = "nullable";
    /**
     * /** to json "length".
     */
    private static final String LENGTH = "length";
    /**
     * ** to json "iskey".
     */
    private static final String ISKEY = "iskey";
    /**
     * the default keyname.
     */
    public static final String OID = "oId";
    /**
     * store all repository filed definition in a Map.
     * <p>
     * key: the name of the repository value: list of all the FieldDefinition
     * </p>
     */
    private static Map<String, List<FieldDefinition>> repositoriesMap = null;

    /**
     * get the RepositoriesMap ,lazy load.
     * 
     * @return Map<String, List<FieldDefinition>>
     */
    public static Map<String, List<FieldDefinition>> getRepositoriesMap() {
        if (repositoriesMap == null) {
            try {
                initRepositoriesMap();
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, "initRepositoriesMap mistake " + e.getMessage(), e);
            }
        }

        return repositoriesMap;
    }

    /**
     * init the repositoriesMap.
     * 
     * @throws JSONException JSONException
     * @throws RepositoryException RepositoryException
     */
    private static void initRepositoriesMap() throws JSONException, RepositoryException {
        final JSONObject jsonObject = Repositories.getRepositoriesDescription();
        if (jsonObject == null) {
            LOGGER.warning("the repository description[repository.json] miss");
            return;
        }

        jsonToRepositoriesMap(jsonObject);
    }

    /**
     * analysis json data structure to java Map structure.
     * 
     * @param jsonObject json Model
     * @throws JSONException JSONException
     */
    private static void jsonToRepositoriesMap(final JSONObject jsonObject) throws JSONException {
        repositoriesMap = new HashMap<String, List<FieldDefinition>>();

        final JSONArray repositoritArray = jsonObject.getJSONArray(REPOSITORIES);

        JSONObject repositoritObject = null;
        JSONObject fieldDefinitionObject = null;

        for (int i = 0; i < repositoritArray.length(); i++) {
            repositoritObject = repositoritArray.getJSONObject(i);
            final String repositoryName = repositoritObject.getString(NAME);

            final List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
            repositoriesMap.put(repositoryName, fieldDefinitions);

            final JSONArray keysJsonArray = repositoritObject.getJSONArray(KEYS);

            FieldDefinition definition = null;
            for (int j = 0; j < keysJsonArray.length(); j++) {
                fieldDefinitionObject = keysJsonArray.getJSONObject(j);
                definition = fillFieldDefinitionData(fieldDefinitionObject);
                fieldDefinitions.add(definition);
            }
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
        final FieldDefinition fieldDefinition = new FieldDefinition();
        fieldDefinition.setName(fieldDefinitionObject.getString(NAME));

        //        final Integer type =
        //                JSONTYPETOJDBCTYPEMAP
        //                        .get(fieldDefinitionObject.getString(TYPE));
        //        if (type == null) {
        //            LOGGER.severe("the type [" + fieldDefinitionObject.getString(TYPE)
        //                    + "] no mapping defined now!!!!");
        //            throw new RuntimeException("the type ["
        //                    + fieldDefinitionObject.getString(TYPE)
        //                    + "] no mapping defined now!!!!");
        //        }

        fieldDefinition.setType(fieldDefinitionObject.getString(TYPE));
        fieldDefinition.setNullable(fieldDefinitionObject.optBoolean(NULLABLE));
        fieldDefinition.setLength(fieldDefinitionObject.optInt(LENGTH));
        fieldDefinition.setIsKey(fieldDefinitionObject.optBoolean(ISKEY));

        /**
         * the default key name is 'old'.
         */
        if (OID.equals(fieldDefinition.getName())) {
            fieldDefinition.setIsKey(true);
        }

        return fieldDefinition;

    }

    /**
     *createTableResult model for view to show.
     *
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
         * 
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * 
         * @param name tableName
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * 
         * @return isSuccess
         */
        public boolean isSuccess() {
            return isSuccess;
        }

        /**
         * 
         * @param isSuccess isSuccess
         */
        public void setSuccess(final boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        /**
         * constructor.
         * 
         * @param name table
         * @param isSuccess isSuccess
         */
        public CreateTableResult(final String name, final boolean isSuccess) {
            super();
            this.name = name;
            this.isSuccess = isSuccess;
        }
    }

    /**
     * initAllTables from json.
     * @return List<CreateTableResult>
     */
    public static List<CreateTableResult> initAllTables() {

        final List<CreateTableResult> results = new ArrayList<JdbcRepositories.CreateTableResult>();
        final Map<String, List<FieldDefinition>> map = getRepositoriesMap();

        boolean isSuccess = false;

        for (String tableName : map.keySet()) {

            try {
                isSuccess = JdbcFactory.createJdbcFactory().createTable(
                        tableName, map.get(tableName));
            } catch (final SQLException e) {
                LOGGER.log(Level.SEVERE,
                           "createTable[" + tableName + "] error", e);
            }

            results.add(new CreateTableResult(tableName, isSuccess));
        }

        return results;

    }

    /**
     * set the repositoriesMap.
     * 
     * @param repositoriesMap  repositoriesMap
     */
    public static void setRepositoriesMap(
            final Map<String, List<FieldDefinition>> repositoriesMap) {
        JdbcRepositories.repositoriesMap = repositoriesMap;
    }

    /**
     * Private constructor.
     */
    private JdbcRepositories() {
    }
}
