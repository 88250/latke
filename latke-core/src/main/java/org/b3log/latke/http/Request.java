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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.*;

/**
 * HTTP request.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 2, 2019
 * @since 2.5.9
 */
public class Request {

    ChannelHandlerContext ctx;
    HttpRequest req;

    private Map<String, String> params;
    private JSONObject json;
    private Map<String, Object> attrs;
    private Set<Cookie> cookies;
    private Session session;
    private boolean staticResource;
    private StringBuilder contentBuilder;

    public Request(final ChannelHandlerContext ctx, final HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
        attrs = new HashMap<>();
        cookies = new HashSet<>();
        params = new HashMap<>();
        contentBuilder = new StringBuilder();
    }

    public void appendContent(final String content) {
        contentBuilder.append(content);
    }

    public void parseJSON() {
        final String jsonStr = contentBuilder.toString();
        json = new JSONObject(jsonStr);
    }

    public void parseForm() {
        final String jsonStr = contentBuilder.toString();
        if (StringUtils.startsWithIgnoreCase(jsonStr, "{\"")) {
            json = new JSONObject(jsonStr);
        } else {
            final QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
            final Map<String, List<String>> uriAttributes = queryDecoder.parameters();
            for (final Map.Entry<String, List<String>> p : uriAttributes.entrySet()) {
                final String key = p.getKey();
                final List<String> vals = p.getValue();
                for (String val : vals) {
                    params.put(key, val);
                }
            }
        }
    }

    public String getHeader(final String name) {
        return req.headers().get(name);
    }

    public String getMethod() {
        return req.method().name();
    }

    public void setMethod(final String method) {
        req.setMethod(HttpMethod.valueOf(method));
    }

    public String getRequestURI() {
        String ret = req.uri();
        ret = StringUtils.substringBefore(ret, "?");
        return ret;
    }

    public void setRequestURI(final String uri) {
        req.setUri(uri);
    }

    public String getContentType() {
        return req.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    public Iterator<String> getHeaderNames() {
        return req.headers().names().iterator();
    }

    public void setParameter(final String name, final String value) {
        params.put(name, value);
    }

    public String getParameter(final String name) {
        return params.get(name);
    }

    public void setParams(final Map<String, String> params) {
        this.params = params;
    }

    public void setJSON(final JSONObject json) {
        this.json = json;
    }

    public JSONObject getJSON() {
        return json;
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

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(final Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    public void addCookie(final String name, final String value) {
        final Cookie cookie = new Cookie(name, value);
        addCookie(cookie);
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public boolean isStaticResource() {
        return staticResource;
    }

    public void setStaticResource(boolean staticResource) {
        this.staticResource = staticResource;
    }
}
