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

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.*;
import org.b3log.latke.repository.jdbc.util.*;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.*;

/**
 * JdbcRepositoryTestCase,now using mysql5.5.9 for test.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Jan 15, 2019
 */
public class JdbcRepositoryTestCase {

    /**
     * jdbcRepository.
     */
    private JdbcRepository jdbcRepository = new JdbcRepository("basetable");

    /**
     * if the database environment is wrong,do not run all the other test.
     */
    private boolean ifRun = true;

    static {
        Latkes.init();
    }

    /**
     * test JsonData.
     *
     * @return Object[][] {{JsonObject},{jsonObject}}.
     */
    @DataProvider(name = "jsonData")
    public static Object[][] createJsonData() {

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("col1", new Integer("100"));
        jsonObject.put("col2", "a测试中文a");
        jsonObject.put("col3", "1.4");
        jsonObject.put("col4", false);

        final Object[][] ret = new Object[][]{{jsonObject}};
        return ret;
    }

    /**
     * createTestTable.
     */
    @BeforeGroups(groups = {"jdbc"})
    public void createTestTable() {
        final StringBuffer createTableSql = new StringBuffer();

        createTableSql.append("   CREATE TABLE IF NOT EXISTS basetable");
        createTableSql.append("   ( ");
        createTableSql.append("  oId VARCHAR(200) NOT NULL, ");
        createTableSql.append("  col1 INT, ");
        createTableSql.append("  col2 VARCHAR(200), ");
        createTableSql.append("  col3 DECIMAL(10,2), ");
        createTableSql.append("  col4 CHAR(1), ");
        createTableSql.append("  PRIMARY KEY (oId) ");
        createTableSql.append(" ) ");
        createTableSql.append(" ENGINE=InnoDB DEFAULT CHARSET=utf8; ");

        try {
            final Connection connection = Connections.getConnection();
            JdbcUtil.executeSql(createTableSql.toString(), connection, false);
            connection.close();
            Latkes.init();

        } catch (final Exception e) {
            // e.printStackTrace();
            ifRun = false;
            System.out.println("skip JdbcRepositoryTestCase test");
        } catch (final ExceptionInInitializerError e) {
            ifRun = false;
            System.out.println("skip JdbcRepositoryTestCase test");

        }

        final List<RepositoryDefinition> repositoryDefinitions = new ArrayList<>();
        final RepositoryDefinition repositoryDefinition = new RepositoryDefinition();
        repositoryDefinitions.add(repositoryDefinition);
        JdbcRepositories.setRepositoryDefinitions(repositoryDefinitions);
        final List<FieldDefinition> dList = new ArrayList<>();

        FieldDefinition definition = new FieldDefinition();
        definition.setName("oId");
        definition.setIsKey(true);
        definition.setType("String");
        dList.add(definition);

        definition = new FieldDefinition();
        definition.setName("col1");
        definition.setType("int");
        dList.add(definition);

        definition = new FieldDefinition();
        definition.setName("col2");
        definition.setType("String");
        dList.add(definition);

        definition = new FieldDefinition();
        definition.setName("col3");
        definition.setType("double");
        dList.add(definition);

        definition = new FieldDefinition();
        definition.setName("col4");
        definition.setType("boolean");
        dList.add(definition);

        repositoryDefinition.setName("basetable");
        repositoryDefinition.setKeys(dList);
    }

    /**
     * add test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    @Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void add(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = jdbcRepository.beginTransaction();
        jdbcRepository.add(jsonObject);
        transaction.commit();

        final JSONObject jsonObjectDb = jdbcRepository.get(jsonObject.getString(JdbcRepositories.getDefaultKeyName()));
        assertNotNull(jsonObjectDb);
    }

    /**
     * update test.
     *
     * @param jsonObject jsonObject
     */
    @Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void update(final JSONObject jsonObject) {
        if (!ifRun) {
            return;
        }

        try {

            final Transaction transaction = jdbcRepository.beginTransaction();
            jdbcRepository.add(jsonObject);

            jsonObject.put("col2", "=================bbbb========================");
            jsonObject.put("col4", true);

            jdbcRepository.update(jsonObject.getString(JdbcRepositories.getDefaultKeyName()), jsonObject);
            transaction.commit();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * remove test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    @Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void remove(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = jdbcRepository.beginTransaction();
        jdbcRepository.add(jsonObject);
        jdbcRepository.remove(jsonObject.getString(JdbcRepositories.getDefaultKeyName()));
        transaction.commit();

        final JSONObject jsonObjectDB = jdbcRepository.get(jsonObject.getString(JdbcRepositories.getDefaultKeyName()));

        assertNull(jsonObjectDB);

    }

    /**
     * hasAndCount test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    @Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void hasAndCount(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final long oCount = jdbcRepository.count();

        final Transaction transaction = jdbcRepository.beginTransaction();
        final String id = jdbcRepository.add(jsonObject);
        transaction.commit();

        assertTrue(jdbcRepository.has(id));

        final long nCount = jdbcRepository.count();
        assertTrue(nCount > oCount);

    }

    /**
     * base query test.
     *
     * @throws Exception Exception
     */
    @Test(groups = {"jdbc"})
    public void queryTest() throws Exception {
        if (!ifRun) {
            return;
        }

        final Query query = new Query();

        final ArrayList<Integer> inList = new ArrayList<Integer>();
        inList.add(new Integer("1"));
        inList.add(new Integer("2"));
        inList.add(new Integer("3"));

        query.setFilter(CompositeFilterOperator.and(
                new PropertyFilter("col1", FilterOperator.EQUAL, new Integer("1")),
                new PropertyFilter("col1", FilterOperator.GREATER_THAN, new Integer("1")),
                new PropertyFilter("col1", FilterOperator.GREATER_THAN_OR_EQUAL, new Integer("1")),
                new PropertyFilter("col1", FilterOperator.LESS_THAN, new Integer("1")),
                new PropertyFilter("col1", FilterOperator.LESS_THAN_OR_EQUAL, new Integer("1")),
                new PropertyFilter("col1", FilterOperator.NOT_EQUAL, new Integer("1")),
                new PropertyFilter("col1", FilterOperator.IN, inList),
                new CompositeFilter(
                        CompositeFilterOperator.OR,
                        Arrays.<Filter>asList(new PropertyFilter("col1", FilterOperator.EQUAL, new Integer("1")),
                                new PropertyFilter("col1", FilterOperator.LESS_THAN_OR_EQUAL, new Integer("1"))))));

        jdbcRepository.get(query);
    }

    /**
     * page query test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    @Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void queryPageTest(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = jdbcRepository.beginTransaction();
        jdbcRepository.add(jsonObject);
        transaction.commit();

        final Query query = new Query();
        query.setFilter(new PropertyFilter("col2", FilterOperator.LIKE, "%中文%"));

        final JSONObject ret = jdbcRepository.get(query);

        assertTrue(ret.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_RECORD_COUNT) > 0);
    }

    /**
     * Like query test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    @Test(groups = "jdbc", dataProvider = "createJsonData")
    public void likeQueryTest(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = jdbcRepository.beginTransaction();
        final int im = 10;
        for (int i = 0; i < im; i++) {
            jdbcRepository.add(jsonObject);
            jsonObject.remove(JdbcRepositories.getDefaultKeyName());
        }
        transaction.commit();

        final Query query = new Query();
        query.setFilter(new PropertyFilter("col1", FilterOperator.EQUAL, new Integer("100")));
        query.select("col1", "col2");
        query.addSort("oId", SortDirection.ASCENDING);
        query.setPageSize(new Integer("4"));
        query.setCurrentPageNum(2);

        final JSONObject ret = jdbcRepository.get(query);

        final int eCount = 4;
        assertEquals(eCount, ret.getJSONArray(Keys.RESULTS).length());
    }

    /**
     * Select test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    @Test(groups = "jdbc", dataProvider = "createJsonData")
    public void selectTest(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = jdbcRepository.beginTransaction();
        final int im = 10;
        for (int i = 0; i < im; i++) {
            jdbcRepository.add(jsonObject);
            jsonObject.remove(JdbcRepositories.getDefaultKeyName());
        }
        transaction.commit();

        List<JSONObject> ret = jdbcRepository.select("SELECT * FROM " + jdbcRepository.getName());
        Assert.assertFalse(ret.isEmpty());

        ret = jdbcRepository.select("SELECT COUNT(*) AS cnt FROM " + jdbcRepository.getName());
        Assert.assertFalse(ret.isEmpty());
    }
}
