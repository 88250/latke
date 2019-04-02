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
package org.b3log.latke.cache.redis;

import org.b3log.latke.Latkes;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.util.Ids;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link RedisCache} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 27, 2018
 * @since 2.3.13
 */
public class RedisCacheTestCase {

    static {
        Latkes.init();
    }

    @Test
    public void put() {
        if (Latkes.RuntimeCache.REDIS != Latkes.getRuntimeCache()) {
            return;
        }

        final Cache cache = CacheFactory.getCache("test");
        Assert.assertNotNull(cache);

        final String k0 = Ids.genTimeMillisId();
        final JSONObject d0 = new JSONObject();
        d0.put("f0", "0");
        d0.put("f1", 1);

        try {
            cache.put(k0, d0);

            final JSONObject d00 = cache.get(k0);
            Assert.assertEquals(d00.toString(), d0.toString());

            cache.clear();

            Assert.assertFalse(cache.contains(k0));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
