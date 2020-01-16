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

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.MockRequest;
import org.b3log.latke.http.Request;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link StaticResources} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 3, 2019
 */
public class StaticResourcesTestCase {

    static {
        Latkes.init();
    }

    /**
     * Tests method {@link StaticResources#isStatic(Request)}.
     */
    @Test
    public void isStatic() {
        FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/css/test.css");
        MockRequest request = new MockRequest(req);

        Assert.assertTrue(StaticResources.isStatic(request));

        req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/images/test.jpg");
        request = new MockRequest(req);
        Assert.assertTrue(StaticResources.isStatic(request));

        req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/js/lib/jquery/jquery.min.js");
        request = new MockRequest(req);
        Assert.assertTrue(StaticResources.isStatic(request));

        req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/test.notExist");
        request = new MockRequest(req);
        Assert.assertFalse(StaticResources.isStatic(request));

        req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/images/test");
        request = new MockRequest(req);
        Assert.assertFalse(StaticResources.isStatic(request));
    }
}
