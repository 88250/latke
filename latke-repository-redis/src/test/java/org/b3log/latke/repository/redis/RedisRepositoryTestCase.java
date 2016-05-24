/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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
package org.b3log.latke.repository.redis;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.CompositeFilter;
import org.b3log.latke.repository.CompositeFilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.json.JSONObject;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Redis repository test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, May 23, 2016
 */
public class RedisRepositoryTestCase {

    /**
     * Redis repository.
     */
    private final RedisRepository redisRepository = new RedisRepository("basetable");

    /**
     * If the database environment is wrong, do not run all the other test.
     */
    private static boolean ifRun = true;

    static {
        Latkes.initRuntimeEnv();

        try {
            RedisRepository.getJedis();
        } catch (final Exception e) {
            ifRun = false;
        }
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

        final Transaction transaction = redisRepository.beginTransaction();
        redisRepository.add(jsonObject);
        transaction.commit();

        final JSONObject jsonObjectDb = redisRepository.get(jsonObject.getString(Keys.OBJECT_ID));
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

            final Transaction transaction = redisRepository.beginTransaction();
            redisRepository.add(jsonObject);

            jsonObject.put("col2", "=================bbbb========================");
            jsonObject.put("col4", true);

            redisRepository.update(jsonObject.getString(JdbcRepositories.getDefaultKeyName()), jsonObject);
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

        final Transaction transaction = redisRepository.beginTransaction();
        redisRepository.add(jsonObject);
        redisRepository.remove(jsonObject.getString(JdbcRepositories.getDefaultKeyName()));
        transaction.commit();

        final JSONObject jsonObjectDB = redisRepository.get(jsonObject.getString(JdbcRepositories.getDefaultKeyName()));

        assertNull(jsonObjectDB);

    }

    /**
     * hasAndCount test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    // @Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void hasAndCount(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final long oCount = redisRepository.count();

        final Transaction transaction = redisRepository.beginTransaction();
        final String id = redisRepository.add(jsonObject);
        transaction.commit();

        assertTrue(redisRepository.has(id));

        final long nCount = redisRepository.count();
        assertTrue(nCount > oCount);

    }

    /**
     * base query test.
     *
     * @throws Exception Exception
     */
    //@Test(groups = {"jdbc"})
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

        redisRepository.get(query);
    }

    /**
     * page query test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    //@Test(groups = {"jdbc"}, dataProvider = "createJsonData")
    public void queryPageTest(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = redisRepository.beginTransaction();
        redisRepository.add(jsonObject);
        transaction.commit();

        final Query query = new Query();
        query.setFilter(new PropertyFilter("col2", FilterOperator.LIKE, "%中文%"));

        final JSONObject ret = redisRepository.get(query);

        assertTrue(ret.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_RECORD_COUNT) > 0);
    }

    /**
     * Like query test.
     *
     * @param jsonObject jsonObject
     * @throws Exception Exception
     */
    //@Test(groups = "jdbc", dataProvider = "createJsonData")
    public void likeQueryTest(final JSONObject jsonObject) throws Exception {
        if (!ifRun) {
            return;
        }

        final Transaction transaction = redisRepository.beginTransaction();
        final int im = 10;
        for (int i = 0; i < im; i++) {
            redisRepository.add(jsonObject);
            jsonObject.remove(JdbcRepositories.getDefaultKeyName());
        }
        transaction.commit();

        final Query query = new Query();
        query.setFilter(new PropertyFilter("col1", FilterOperator.EQUAL, new Integer("100")));
        query.addProjection("col1", String.class);
        query.addProjection("col2", String.class);
        query.addSort("oId", SortDirection.ASCENDING);
        query.setPageSize(new Integer("4"));
        query.setCurrentPageNum(2);

        final JSONObject ret = redisRepository.get(query);

        final int eCount = 4;
        assertEquals(eCount, ret.getJSONArray(Keys.RESULTS).length());
    }
}
