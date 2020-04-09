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
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.util.StaticResources;

import java.util.Set;

/**
 * Http server handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.0, Mar 13, 2020
 * @since 3.0.0
 */
final class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ServerHandler.class);

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest fullHttpRequest) {
        setSchemeHostPort(fullHttpRequest);
        final Request request = new Request(ctx, fullHttpRequest);
        final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        final Response response = new Response(ctx, res);
        response.setKeepAlive(HttpUtil.isKeepAlive(request.req));
        if (!StaticResources.isStatic(request)) {
            // 解析查询字符串
            request.parseQueryStr();

            // 解析请求体
            String contentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
            if (StringUtils.isNotBlank(contentType)) {
                contentType = StringUtils.substringBefore(contentType, ";");
                if (StringUtils.equalsIgnoreCase(contentType, "multipart/form-data")) {
                    request.parseFormData();
                } else {
                    request.parseForm();
                }
            } else {
                request.parseForm();
            }

            // 处理 Cookie
            handleCookie(request, response);
        } else {
            // 标识为静态资源文件
            request.setStaticResource(true);
        }

        // 分发处理
        final RequestContext context = Dispatcher.handle(request, response);

        // 释放资源
        release(context);
    }

    private void setSchemeHostPort(final FullHttpRequest fullHttpRequest) {
        final HttpHeaders headers = fullHttpRequest.headers();
        if (null != headers) {
            final String scheme = headers.get("scheme");
            if (StringUtils.isNotBlank(scheme)) {
                Latkes.setScheme(scheme);
            }
            final String host = headers.get("host");
            if (StringUtils.isNotBlank(host)) {
                if (StringUtils.containsIgnoreCase(host, ":")) {
                    final String name = StringUtils.split(host, ":")[0];
                    final String port = StringUtils.split(host, ":")[1];
                    Latkes.setHost(name);
                    Latkes.setPort(port);
                } else {
                    Latkes.setHost(host);
                }
            }
        }
    }

    private void release(final RequestContext context) {
        final Request request = context.getRequest();
        if (null != request.httpDecoder) {
            request.httpDecoder.destroy();
        }

        Latkes.clearSchemeHostPort();
    }

    private void handleCookie(final Request request, final Response response) {
        final boolean secure = StringUtils.equalsIgnoreCase(Latkes.getServerScheme(), "https");
        Session session = null;
        final boolean enabledSession = Latkes.isEnabledSession();
        final String cookieStr = request.getHeader(HttpHeaderNames.COOKIE.toString());
        if (StringUtils.isNotBlank(cookieStr)) {
            final Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (final Cookie cookie : cookies) {
                if (!enabledSession) {
                    request.addCookie(new org.b3log.latke.http.Cookie(cookie));
                    continue;
                }

                if (cookie.name().equals(Session.LATKE_SESSION_ID)) {
                    final String cookieSessionId = cookie.value();
                    if (!Sessions.contains(cookieSessionId)) {
                        session = createSessionCookie(request, response, secure);
                    } else {
                        session = Sessions.get(cookieSessionId);
                        final org.b3log.latke.http.Cookie c = new org.b3log.latke.http.Cookie(Session.LATKE_SESSION_ID, session.getId());
                        c.setHttpOnly(true);
                        c.setSecure(secure);
                        request.addCookie(c);
                        response.addCookie(c);
                    }
                } else {
                    request.addCookie(new org.b3log.latke.http.Cookie(cookie));
                    response.addCookie(new org.b3log.latke.http.Cookie(cookie));
                }
            }
        }

        if (null == session && enabledSession) {
            session = createSessionCookie(request, response, secure);
        }
        request.session = session;
    }

    private Session createSessionCookie(final Request request, final Response response, final boolean secure) {
        final Session ret = Sessions.add();
        final org.b3log.latke.http.Cookie c = new org.b3log.latke.http.Cookie(Session.LATKE_SESSION_ID, ret.getId());
        c.setHttpOnly(true);
        c.setSecure(secure);
        request.addCookie(c);
        response.addCookie(c);

        return ret;
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
//        LOGGER.log(Level.WARN, "Server handle failed", cause);
        ctx.close();
    }
}