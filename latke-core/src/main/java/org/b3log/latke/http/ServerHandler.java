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
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.StaticResources;

import java.util.Set;

/**
 * Http server handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Nov 5, 2019
 * @since 3.0.0
 */
final class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class);

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest fullHttpRequest) {
        final Request request = new Request(ctx, fullHttpRequest);

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

        if (!StaticResources.isStatic(request)) {
            // 非静态资源文件处理 Cookie
            handleCookie(request);
        } else {
            // 标识为静态资源文件
            request.setStaticResource(true);
        }

        final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        final Response response = new Response(ctx, res);
        response.setKeepAlive(HttpUtil.isKeepAlive(request.req));
        response.setCookies(request.getCookies());

        // 分发处理
        Dispatcher.handle(request, response);
    }

    private void handleCookie(final Request request) {
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
                        session = createSessionCookie(request, secure);
                    } else {
                        session = Sessions.get(cookieSessionId);
                        final org.b3log.latke.http.Cookie c = new org.b3log.latke.http.Cookie(Session.LATKE_SESSION_ID, session.getId());
                        c.setHttpOnly(true);
                        c.setSecure(secure);
                        request.addCookie(c);
                    }
                } else {
                    request.addCookie(new org.b3log.latke.http.Cookie(cookie));
                }
            }
        } else {
            if (enabledSession) {
                session = createSessionCookie(request, secure);
            }
        }

        if (null == session && enabledSession) {
            session = createSessionCookie(request, secure);
        }
        request.session = session;
    }

    private Session createSessionCookie(final Request request, final boolean secure) {
        final Session ret = Sessions.add();
        final org.b3log.latke.http.Cookie c = new org.b3log.latke.http.Cookie(Session.LATKE_SESSION_ID, ret.getId());
        c.setHttpOnly(true);
        c.setSecure(secure);
        request.addCookie(c);

        return ret;
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
//        LOGGER.log(Level.WARN, "Server handle failed", cause);
        ctx.close();
    }
}