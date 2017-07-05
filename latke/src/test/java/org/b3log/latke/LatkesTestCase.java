package org.b3log.latke;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link Latkes} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 6, 2017
 * @since 2.3.13
 */
public class LatkesTestCase {

    @Test
    public void initRuntimeEnv() {
        Latkes.initRuntimeEnv();

        final RuntimeCache runtimeCache = Latkes.getRuntimeCache();
        Assert.assertEquals(runtimeCache, RuntimeCache.REDIS);

        final String redisHost = Latkes.getLocalProperty("redis.host");
        Assert.assertEquals(redisHost, "localhost");
        final int redisPort = Integer.valueOf(Latkes.getLocalProperty("redis.port"));
        Assert.assertEquals(redisPort, 6379);
    }
}
