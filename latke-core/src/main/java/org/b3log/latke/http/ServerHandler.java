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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http server handler.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 2, 2019
 * @since 2.5.9
 */
public final class ServerHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * Request.
     */
    private HttpRequest req;

    /**
     * Buffer that stores the response content
     */
    private final StringBuilder buf = new StringBuilder();

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) {
        if (msg instanceof HttpRequest) {
            this.req = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(req)) {
                send100Continue(ctx);
            }

            buf.setLength(0);
            buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
            buf.append("===================================\r\n");

            buf.append("VERSION: ").append(req.protocolVersion()).append("\r\n");
            buf.append("HOSTNAME: ").append(req.headers().get(HttpHeaderNames.HOST, "unknown")).append("\r\n");
            buf.append("REQUEST_URI: ").append(req.uri()).append("\r\n\r\n");

            final HttpHeaders headers = req.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h : headers) {
                    CharSequence key = h.getKey();
                    CharSequence value = h.getValue();
                    buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
                }
                buf.append("\r\n");
            }

            final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
            final Map<String, List<String>> params = queryStringDecoder.parameters();
            if (!params.isEmpty()) {
                for (Map.Entry<String, List<String>> p : params.entrySet()) {
                    String key = p.getKey();
                    List<String> vals = p.getValue();
                    for (String val : vals) {
                        buf.append("PARAM: ").append(key).append(" = ").append(val).append("\r\n");
                    }
                }
                buf.append("\r\n");
            }

            appendDecoderResult(buf, req);
        }

        if (msg instanceof HttpContent) {
            final HttpContent httpContent = (HttpContent) msg;

            final ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                buf.append("CONTENT: ");
                buf.append(content.toString(CharsetUtil.UTF_8));
                buf.append("\r\n");
                appendDecoderResult(buf, req);
            }

            if (msg instanceof LastHttpContent) {
                buf.append("END OF CONTENT\r\n");

                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()) {
                    buf.append("\r\n");
                    for (CharSequence name : trailer.trailingHeaders().names()) {
                        for (CharSequence value : trailer.trailingHeaders().getAll(name)) {
                            buf.append("TRAILING HEADER: ");
                            buf.append(name).append(" = ").append(value).append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                final DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

                final String cookieString = req.headers().get(HttpHeaderNames.COOKIE);
                if (cookieString != null) {
                    final Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
                    for (Cookie cookie : cookies) {
                        res.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                    }
                }

                final Request request = new Request(ctx, req);

                final Response response = new Response(ctx, req, res);
                Dispatcher.handle(request, response);
            }
        }
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        final DecoderResult result = o.decoderResult();
        if (result.isSuccess()) {
            return;
        }

        buf.append(".. WITH DECODER FAILURE: ");
        buf.append(result.cause());
        buf.append("\r\n");
    }

    private static void send100Continue(final ChannelHandlerContext ctx) {
        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}