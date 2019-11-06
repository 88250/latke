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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Websocket handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 6, 2019
 * @since 3.0.2
 */
final class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private WebSocketSession webSocketSession;
    private WebSocketChannel webSocketChannel;
    private String uri;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            handleHttpRequest(ctx, (HttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        } else {
            ReferenceCountUtil.retain(msg);
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
        if (isWebSocketRequest(req)) {
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(req.uri(), null, true);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
                webSocketSession = new WebSocketSession(ctx);

                // 解析查询字符串
                final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
                final Map<String, List<String>> params = queryStringDecoder.parameters();
                if (!params.isEmpty()) {
                    for (final Map.Entry<String, List<String>> p : params.entrySet()) {
                        final String key = p.getKey();
                        final List<String> vals = p.getValue();
                        for (final String val : vals) {
                            webSocketSession.params.put(key, val);
                        }
                    }
                }

                // 关联处理 HTTP 会话
                Session session = null;
                final String cookieStr = req.headers().get(HttpHeaderNames.COOKIE.toString());
                if (StringUtils.isNotBlank(cookieStr)) {
                    final Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
                    for (final io.netty.handler.codec.http.cookie.Cookie cookie : cookies) {
                        if (cookie.name().equals("LATKE_SESSION_ID")) {
                            final String cookieSessionId = cookie.value();
                            if (!Sessions.contains(cookieSessionId)) {
                                session = Sessions.add();
                            } else {
                                session = Sessions.get(cookieSessionId);
                            }
                            break;
                        }
                    }
                }
                if (null == session) {
                    session = Sessions.add();
                }

                webSocketSession.session = session;

                CompletableFuture.completedFuture(webSocketSession).thenAcceptAsync(webSocketChannel::onConnect, ctx.executor());
            }
        } else {
            ReferenceCountUtil.retain(req);
            ctx.fireChannelRead(req);
        }
    }


    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            CompletableFuture.completedFuture(webSocketSession).thenAcceptAsync(webSocketChannel::onClose);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException("unsupported frame type: " + frame.getClass().getName());
        }

        CompletableFuture.completedFuture(new WebSocketChannel.Message(((TextWebSocketFrame) frame).text(), webSocketSession))
                .thenAcceptAsync(webSocketChannel::onMessage, ctx.executor());
    }


    private boolean isWebSocketRequest(final HttpRequest req) {
        uri = req.uri();
        uri = StringUtils.substringBefore(uri, "?");

        return (webSocketChannel = Dispatcher.webSocketChannels.get(uri)) != null
                && req.decoderResult().isSuccess()
                && "websocket".equals(req.headers().get("Upgrade"));
    }
}
