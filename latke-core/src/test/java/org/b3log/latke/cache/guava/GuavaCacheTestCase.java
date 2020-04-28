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
package org.b3log.latke.cache.guava;

import org.b3log.latke.Latkes;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.util.Ids;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * {@link GuavaCache} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Apr 28, 2020
 * @since 2.4.58
 */
public class GuavaCacheTestCase {

    static {
        Latkes.init();
    }

    @Test
    public void put() {
        if (Latkes.RuntimeCache.LOCAL_LRU != Latkes.getRuntimeCache()) {
            return;
        }

        final Cache cache = CacheFactory.getCache("test");
        Assert.assertNotNull(cache);

        final String k0 = Ids.genTimeMillisId();
        final JSONObject d0 = new JSONObject();
        d0.put("f0", "0");
        d0.put("f1", 1);

        cache.put(k0, d0);

        final JSONObject d00 = cache.get(k0);
        Assert.assertEquals(d00.toString(), d0.toString());

        cache.clear();
        Assert.assertFalse(cache.contains(k0));
    }

    @Test
    public void expire() throws Exception {
        if (Latkes.RuntimeCache.LOCAL_LRU != Latkes.getRuntimeCache()) {
            return;
        }

        final Cache cache = CacheFactory.getCache("test", 1);
        Assert.assertNotNull(cache);

        final String k0 = Ids.genTimeMillisId();
        final JSONObject d0 = new JSONObject();
        d0.put("f0", "0");
        d0.put("f1", 1);

        cache.put(k0, d0);

        final JSONObject d00 = cache.get(k0);
        Assert.assertEquals(d00.toString(), d0.toString());

        TimeUnit.MILLISECONDS.sleep(1100);

        Assert.assertFalse(cache.contains(k0));
    }
}
