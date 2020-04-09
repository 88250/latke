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
package org.b3log.latke.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * {@link Reflections} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Oct 14, 2013
 */
public class ReflectionsTestCase {

    public void testMethod1(final String para1, final int para2) {
    }

    public void testMethod2(final RequestContext context) throws IOException {
    }

    @Test
    public void getMethodVariableNames() {
        String[] paraNames = Reflections.getMethodVariableNames(ReflectionsTestCase.class, "testMethod1",
                new Class<?>[]{String.class, int.class});

        Assert.assertEquals(paraNames.length, 2);
        Assert.assertEquals(paraNames[0], "para1");
        Assert.assertEquals(paraNames[1], "para2");

        paraNames = Reflections.getMethodVariableNames(ReflectionsTestCase.class, "testMethod2",
                new Class<?>[]{RequestContext.class});
        Assert.assertEquals(paraNames.length, 1);
        Assert.assertEquals(paraNames[0], "context");

        paraNames = Reflections.getMethodVariableNames(FeedProcessor.class, "tagArticlesRSS",
                new Class<?>[]{RequestContext.class});
        Assert.assertEquals(paraNames.length, 1);
        Assert.assertEquals(paraNames[0], "context");
    }
}

/**
 * Test class.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Oct 14, 2013
 * @since 0.3.1
 */
class FeedProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(FeedProcessor.class);

    /**
     * Blog articles Atom output.
     *
     * @param context the specified context
     */
    public void blogArticlesAtom(final RequestContext context) {
    }

    /**
     * Tag articles Atom output.
     *
     * @param context the specified context
     */
    public void tagArticlesAtom(final RequestContext context) {
    }

    /**
     * Blog articles RSS output.
     *
     * @param context the specified context
     */
    public void blogArticlesRSS(final RequestContext context) {
    }

    /**
     * Tag articles RSS output.
     *
     * @param context the specified context
     */
    public void tagArticlesRSS(final RequestContext context) {
    }
}
