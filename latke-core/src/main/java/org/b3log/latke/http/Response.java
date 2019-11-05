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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import org.b3log.latke.http.handler.ContextHandlerMeta;
import org.b3log.latke.http.renderer.AbstractResponseRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * HTTP response.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 3, 2019
 * @since 3.0.0
 */
public class Response {

    private static final Logger LOGGER = Logger.getLogger(Response.class);

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

    public String getContentStr() {
        return new String(content, CharsetUtil.UTF_8);
    }

    public byte[] getContent() {
        return content;
    }

    public void sendError(final int status) {
        setStatus(status);
        if (null != Dispatcher.errorHandleRouter) {
            try {
                final ContextHandlerMeta contextHandlerMeta = Dispatcher.errorHandleRouter.toContextHandlerMeta();
                final Method invokeHolder = contextHandlerMeta.getInvokeHolder();
                final BeanManager beanManager = BeanManager.getInstance();
                final Object classHolder = beanManager.getReference(invokeHolder.getDeclaringClass());
                context.pathVar("statusCode", String.valueOf(status));
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

    public void sendContent(final byte[] content) {
        this.content = content;
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
