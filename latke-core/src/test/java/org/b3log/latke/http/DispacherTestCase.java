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
package org.b3log.latke.http;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.*;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor test.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Feb 9, 2020
 * @since 3.2.4
 */
public class DispacherTestCase {

    static {
        Latkes.init();
    }

    @BeforeTest
    public void beforeTest() {
        final List<Class<?>> classes = new ArrayList<>();
        classes.add(TestProcessor.class);
        BeanManager.start(classes);
        final TestProcessor testProcessor = BeanManager.getInstance().getReference(TestProcessor.class);
        final TestMiddleware testMidware = BeanManager.getInstance().getReference(TestMiddleware.class);
        Dispatcher.group().middlewares(testMidware::handle).get("/a", testProcessor::a);
        Dispatcher.mapping();
    }

    @AfterTest
    public void afterTest() {
        BeanManager.close();
    }

    @Test
    public void a() {
        final FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/a");
        final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        final MockRequest request = new MockRequest(req);
        final MockResponse response = new MockResponse(res);

        final RequestContext context = Dispatcher.handle(request, response);
        Assert.assertEquals(context.attr("a"), "a");
        Assert.assertEquals(context.attr("before"), "before");
        Assert.assertEquals(context.attr("after"), "after");
    }
}
