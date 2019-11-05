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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.StaticResources;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Http server handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 2, 2019
 * @since 3.0.0
 */
public final class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

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
        final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.req.uri());
        final Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (final Map.Entry<String, List<String>> p : params.entrySet()) {
                final String key = p.getKey();
                final List<String> vals = p.getValue();
                for (final String val : vals) {
                    request.setParameter(key, val);
                }
            }
        }

        // 解析请求体
        final ByteBuf content = fullHttpRequest.content();
        final String contentText = (content.toString(CharsetUtil.UTF_8));
        String contentType = request.getHeader(HttpHeaderNames.CONTENT_TYPE.toString());
        if (StringUtils.isNotBlank(contentType)) {
            contentType = StringUtils.substringBefore(contentType, ";");
            switch (contentType) {
                case "application/json":
                    request.parseJSON(contentText);
                    break;
                case "application/x-www-form-urlencoded":
                    request.parseForm(contentText);
                    break;
                case "multipart/form-data":
                    // TODO: 文件上传
                    LOGGER.log(Level.WARN, "TODO: handle file upload");
                    break;
            }
        }

        if (!StaticResources.isStatic(request)) {
            // 非静态资源文件处理 Cookie
            final String cookieStr = request.getHeader(HttpHeaderNames.COOKIE.toString());
            if (StringUtils.isNotBlank(cookieStr)) {
                final Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
                for (final Cookie cookie : cookies) {
                    request.addCookie(new org.b3log.latke.http.Cookie(cookie));
                }
            }
            if (!request.getCookies().stream().anyMatch(cookie -> "LATKE_SESSION_ID".equals(cookie.getName()) && !Sessions.contains(cookie.getValue()))) {
                request.addCookie("LATKE_SESSION_ID", Sessions.add().getId());
            }
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

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.log(Level.ERROR, "Server handle failed", cause);
        ctx.close();
    }
}