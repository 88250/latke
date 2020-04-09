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
