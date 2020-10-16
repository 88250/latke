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

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.util.URLs;
import org.json.JSONObject;

import java.util.*;

/**
 * HTTP request.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, Oct 17, 2020
 * @since 3.0.0
 */
public class Request {

    private static final Logger LOGGER = LogManager.getLogger(Request.class);

    private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(true);

    ChannelHandlerContext ctx;
    FullHttpRequest req;
    HttpPostRequestDecoder httpDecoder;
    RequestContext context;

    Map<String, String> params = new HashMap<>();
    JSONObject json = new JSONObject();
    Map<String, Object> attrs = new HashMap<>();
    byte[] bytes;
    Map<String, List<org.b3log.latke.http.FileUpload>> files = new HashMap<>();
    Set<Cookie> cookies = new HashSet<>();
    Session session;
    boolean staticResource;

    public Request(final ChannelHandlerContext ctx, final FullHttpRequest req) {
        this.ctx = ctx;
        this.req = req;
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

    public String getQueryString() {
        String ret = req.uri();
        return StringUtils.substringAfter(ret, "?");
    }

    public String getString() {
        return org.apache.commons.codec.binary.StringUtils.newStringUtf8(bytes);
    }

    public void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
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
        String ret = ctx.channel().remoteAddress().toString();
        if (StringUtils.startsWith(ret, "/")) {
            ret = ret.substring(1);
        }
        return StringUtils.substringBeforeLast(ret, ":");
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

    public FileUpload getFileUpload(final String name) {
        final List<FileUpload> fileUploads = getFileUploads(name);
        if (fileUploads.isEmpty()) {
            return null;
        }

        return fileUploads.get(0);
    }

    void parseQueryStr() {
        parseAttrs(req.uri(), true);
    }

    void parseForm() {
        try {
            final String content = (req.content().toString(CharsetUtil.UTF_8));
            if (StringUtils.startsWithIgnoreCase(content, "%7B")) {
                json = new JSONObject(URLs.decode(content));
            } else if (StringUtils.startsWithIgnoreCase(content, "{")) {
                json = new JSONObject(content);
            } else {
                parseAttrs(content, false);
            }
            bytes = org.apache.commons.codec.binary.StringUtils.getBytesUtf8(content);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Parses request [uri=" + req.uri() + ", remoteAddr=" + getRemoteAddr() + ", body=" + bytes + "] failed: " + e.getMessage());
        }
    }

    void parseFormData() {
        try {
            httpDecoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, req);
            httpDecoder.setDiscardThreshold(0);
            httpDecoder.offer(req);

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
            LOGGER.log(Level.ERROR, "Parses request [uri=" + req.uri() + ", remoteAddr=" + getRemoteAddr() + ", body=" + bytes + "] failed: " + e.getMessage());
        }
    }

    private void parseAttrs(final String paris, final boolean hasPath) {
        final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(paris, hasPath);
        final Map<String, List<String>> parameters = queryStringDecoder.parameters();
        for (final Map.Entry<String, List<String>> p : parameters.entrySet()) {
            final String key = p.getKey();
            final List<String> vals = p.getValue();
            // 这里没有实现 name -> List，主要是为了兼容老应用，等有空需要改造一下，按照规范来
            for (final String val : vals) {
                params.put(key, val);
            }
        }
    }

}
