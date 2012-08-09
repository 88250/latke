/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.latke.repository.gae;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * {@link GAERepository} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 29, 2011
 */
public class GAERepositoryTestCase {

    /**
     * Local service test helper.
     */
    private final LocalServiceTestHelper localServiceTestHelper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                                       new LocalMemcacheServiceTestConfig());

    /**
     * Before class.
     */
    @BeforeClass
    public void beforeClass() {
        localServiceTestHelper.setUp();

        Latkes.initRuntimeEnv();
    }

    /**
     * After class.
     */
    @AfterClass
    public void afterClass() {
        localServiceTestHelper.tearDown();

        Latkes.shutdown();
    }

    /**
     * Test add, get, etc.
     * 
     * @throws Exception exception 
     */
    @Test
    public void add() throws Exception {
        final GAERepository repository = new GAERepository("test repository");

        final GAETransaction transaction = repository.beginTransaction();

        try {
            final JSONObject json = new JSONObject();

            json.put(Keys.OBJECT_ID, "88250");
            json.put("key1", 1);
            json.put("key2", 2D);
            json.put("key3", true);

            repository.add(json);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }

        final long count = repository.count();

        assertEquals(count, 1);

        final JSONObject json = repository.get("88250");
        assertNotNull(json);

        assertTrue(json.optBoolean("key3"));

    }
}
