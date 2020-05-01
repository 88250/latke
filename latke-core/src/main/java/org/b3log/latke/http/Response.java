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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.handler.ContextHandlerMeta;
import org.b3log.latke.http.renderer.AbstractResponseRenderer;
import org.b3log.latke.ioc.BeanManager;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * HTTP response.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, May 1, 2020
 * @since 3.0.0
 */
public class Response {

    private static final Logger LOGGER = LogManager.getLogger(Response.class);

    ChannelHandlerContext ctx;
    RequestContext context;

    private HttpResponse res;
    private boolean commited;
    private boolean keepAlive;
    private byte[] content;
    private Set<Cookie> cookies;

    public Response(final ChannelHandlerContext ctx, final HttpResponse res) {
        this.ctx = ctx;
        this.res = res;
        cookies = new HashSet<>();
    }

    public void setKeepAlive(final boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isCommitted() {
        return commited;
    }

    public String getHeader(final String name) {
        return res.headers().get(name);
    }

    public void addHeader(final String name, final String value) {
        res.headers().add(name, value);
    }

    public void setHeader(final String name, final String value) {
        res.headers().set(name, value);
    }

    public void setStatus(final int status) {
        res.setStatus(HttpResponseStatus.valueOf(status));
    }

    public String getContentType() {
        return res.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    public void setContentType(final String contentType) {
        res.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    public Iterator<String> getHeaderNames() {
        return res.headers().names().iterator();
    }

    public void addCookie(final Cookie cookie) {
        final String name = cookie.getName();
        cookies.removeIf(c -> c.getName().equals(cookie.getName()));
        cookies.add(cookie);
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(final Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    public String getString() {
        return StringUtils.newStringUtf8(content);
    }

    public byte[] getBytes() {
        return content;
    }

    public void sendError0(final int status) {
        setStatus(status);
        writeResponse();
    }

    public void sendError(final int status) {
        sendError(status, null);
    }

    public void sendError(final int status, final Map<String, Object> dataModel) {
        setStatus(status);
        if (null != Dispatcher.errorHandleRouter) {
            try {
                context.attr(RequestContext.ERROR_CODE, status);
                final ContextHandlerMeta contextHandlerMeta = Dispatcher.errorHandleRouter.toContextHandlerMeta();
                final Method invokeHolder = contextHandlerMeta.getInvokeHolder();
                final BeanManager beanManager = BeanManager.getInstance();
                final Object classHolder = beanManager.getReference(invokeHolder.getDeclaringClass());
                context.pathVar("statusCode", String.valueOf(status));
                context.attr("dataModel", dataModel);
                invokeHolder.invoke(classHolder, context);
                final AbstractResponseRenderer renderer = context.getRenderer();
                renderer.render(context);
                return;
            } catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Use error handler failed", e);
            }
        }
        writeResponse();
    }

    public void sendRedirect(final String location) {
        setHeader(HttpHeaderNames.LOCATION.toString(), location);
        setStatus(HttpResponseStatus.FOUND.code());
        writeResponse();
    }

    public void sendBytes(final byte[] bytes) {
        this.content = bytes;
        writeResponse();
    }

    public void sendString(final String string) {
        this.content = StringUtils.getBytesUtf8(string);
        writeResponse();
    }

    public void send() {
        writeResponse();
    }

    private void writeResponse() {
        final ByteBuf contentBuf = null != content ? Unpooled.copiedBuffer(content) : Unpooled.EMPTY_BUFFER;
        res = ((FullHttpResponse) res).replace(contentBuf);
        if (keepAlive) {
            res.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, ((FullHttpResponse) res).content().readableBytes());
            res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        for (final Cookie cookie : cookies) {
            res.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie.cookie));
        }

        commited = true;

        if (null != ctx) {
            ctx.write(res);
            if (!keepAlive) {
                ctx.write(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            ctx.flush();
        }
    }
}
