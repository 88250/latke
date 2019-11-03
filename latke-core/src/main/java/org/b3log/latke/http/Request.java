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

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP request.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 2, 2019
 * @since 2.5.9
 */
public class Request {

    private ChannelHandlerContext ctx;
    private HttpRequest req;

    private Map<String, List<String>> params;
    private Map<String, Object> attrs;
    private Cookie[] cookies;

    public Request(final ChannelHandlerContext ctx, final HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
        params = new ConcurrentHashMap<>();
        attrs = new ConcurrentHashMap<>();
    }

    public String getHeader(final String name) {
        return req.headers().get(name);
    }

    public String getMethod() {
        return req.method().name();
    }

    public String getRequestURI() {
        return req.uri();
    }

    public String getContentType() {
        return req.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    public Iterator<String> getHeaderNames() {
        return req.headers().names().iterator();
    }

    public String getParameter(final String name) {
        final List<String> list = params.getOrDefault(name, Collections.emptyList());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public Object getAttribute(final String name) {
        return attrs.get(name);
    }

    public void setAttribute(final String name, final Object value) {
        attrs.put(name, value);
    }

    public String getRemoteAddr() {
        return ctx.channel().remoteAddress().toString();
    }

    public String getScheme() {
        return "http";
    }

    public String getServerName() {
        return "localhost";
    }

    public int getServerPort() {
        return 8080;
    }

    public Cookie[] getCookies() {
        return cookies;
    }
}
