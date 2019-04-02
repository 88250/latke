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
package org.b3log.latke.util;

import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
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

    @RequestProcessing(value = {"/"}, method = {HttpMethod.GET, HttpMethod.HEAD})
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
@RequestProcessor
class FeedProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(FeedProcessor.class);

    /**
     * Blog articles Atom output.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = {"/blog-articles-feed.do"}, method = {HttpMethod.GET, HttpMethod.HEAD})
    public void blogArticlesAtom(final RequestContext context) {
    }

    /**
     * Tag articles Atom output.
     *
     * @param context the specified context
     * @throws IOException io exception
     */
    @RequestProcessing(value = {"/tag-articles-feed.do"}, method = {HttpMethod.GET, HttpMethod.HEAD})
    public void tagArticlesAtom(final RequestContext context) throws IOException {
    }

    /**
     * Blog articles RSS output.
     *
     * @param context the specified context
     */
    @RequestProcessing(value = {"/blog-articles-rss.do"}, method = {HttpMethod.GET, HttpMethod.HEAD})
    public void blogArticlesRSS(final RequestContext context) {
    }

    /**
     * Tag articles RSS output.
     *
     * @param context the specified context
     * @throws IOException io exception
     */
    @RequestProcessing(value = {"/tag-articles-rss.do"}, method = {HttpMethod.GET, HttpMethod.HEAD})
    public void tagArticlesRSS(final RequestContext context) throws IOException {
    }
}
