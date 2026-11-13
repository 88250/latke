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

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Request parsing tests using synthetic local requests.
 */
public class RequestTestCase {

    @Test
    public void parsesJsonObjectWithLeadingWhitespace() {
        final String body = " \t\r\n{\"name\":\"synthetic\"}";
        final DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.POST,
                "/",
                Unpooled.copiedBuffer(body, CharsetUtil.UTF_8)
        );
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

        try {
            final MockRequest mockRequest = new MockRequest(request);
            mockRequest.parseForm();
            Assert.assertEquals(mockRequest.getJSON().getString("name"), "synthetic");
        } finally {
            request.release();
        }
    }
}
