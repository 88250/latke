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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.json.JSONObject;

import java.util.*;

/**
 * HTTP request.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Nov 5, 2019
 * @since 3.0.0
 */
public class Request {

    private static final Logger LOGGER = Logger.getLogger(Request.class);

    ChannelHandlerContext ctx;
    HttpRequest req;
    HttpPostRequestDecoder httpDecoder;
    RequestContext context;

    private Map<String, String> params;
    private JSONObject json;
    private Map<String, Object> attrs;
    private Map<String, List<org.b3log.latke.http.FileUpload>> files;
    private Set<Cookie> cookies;
    private Session session;
    private boolean staticResource;

    public Request(final ChannelHandlerContext ctx, final HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
        attrs = new HashMap<>();
        files = new HashMap<>();
        cookies = new HashSet<>();
        params = new HashMap<>();
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

    public Set<String> getParameterNames() {
        return params.keySet();
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
        if (StringUtils.isBlank(cookie.getPath())) {
            cookie.setPath(Latkes.getContextPath());
        }
        if (StringUtils.isBlank(cookie.getPath())) {
            cookie.setPath("/");
        }
        cookies.add(cookie);
    }

    public void addCookie(final String name, final String value) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
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

    public List<FileUpload> getFileUploads(final String name) {
        return files.getOrDefault(name, Collections.emptyList());
    }

    void parseJSON(final FullHttpRequest fullHttpRequest) {
        json = new JSONObject((fullHttpRequest.content().toString(CharsetUtil.UTF_8)));
    }

    void parseForm(final FullHttpRequest fullHttpRequest) {
        final String content = (fullHttpRequest.content().toString(CharsetUtil.UTF_8));
        if (StringUtils.startsWithIgnoreCase(content, "{\"")) {
            json = new JSONObject(content);
        } else {
            final QueryStringDecoder queryDecoder = new QueryStringDecoder(content, false);
            final Map<String, List<String>> uriAttributes = queryDecoder.parameters();
            for (final Map.Entry<String, List<String>> p : uriAttributes.entrySet()) {
                final String key = p.getKey();
                final List<String> vals = p.getValue();
                // 这里没有实现 name -> List，主要是为了兼容老应用，等有空需要改造一下，按照规范来
                for (final String val : vals) {
                    params.put(key, val);
                }
            }
        }
    }

    void parseFormData() {
        try {
            while (httpDecoder.hasNext()) {
                final InterfaceHttpData data = httpDecoder.next();
                if (InterfaceHttpData.HttpDataType.FileUpload == data.getHttpDataType()) {
                    final FileUpload fileUpload = new FileUpload();
                    fileUpload.fileUpload = (io.netty.handler.codec.http.multipart.FileUpload) data;
                    files.computeIfAbsent(fileUpload.getName(), k -> new ArrayList<>()).add(fileUpload);
                } else {
                    final Attribute attribute = (Attribute) data;
                    params.put(attribute.getName(), attribute.getValue());
                }
            }
        } catch (final HttpPostRequestDecoder.EndOfDataDecoderException e) {
            // ignore
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Parse form data failed", e);
        }
    }
}
