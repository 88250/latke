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
package org.b3log.latke;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link Latkes} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jun 8, 2019
 * @since 2.3.13
 */
public class LatkesTestCase {

    @Test
    public void init() {
        Latkes.init();

        final Latkes.RuntimeCache runtimeCache = Latkes.getRuntimeCache();
        Assert.assertEquals(runtimeCache, Latkes.RuntimeCache.LOCAL_LRU);

        final String redisHost = Latkes.getLocalProperty("redis.host");
        Assert.assertEquals(redisHost, "localhost");
        final int redisPort = Integer.valueOf(Latkes.getLocalProperty("redis.port"));
        Assert.assertEquals(redisPort, 6379);
    }
}
