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
