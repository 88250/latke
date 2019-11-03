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
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class);

    /**
     * Request.
     */
    private HttpRequest req;

    private final Map<String, String> params = new HashMap<>();
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


            final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.uri());
            final Map<String, List<String>> params = queryStringDecoder.parameters();
            if (!params.isEmpty()) {
                for (final Map.Entry<String, List<String>> p : params.entrySet()) {
                    final String key = p.getKey();
                    final List<String> vals = p.getValue();
                    for (final String val : vals) {
                        this.params.put(key, val);
                    }
                }
            }
        } else if (msg instanceof HttpContent) {
            final ByteBuf content = ((HttpContent) msg).content();
            buf.append(content.toString(CharsetUtil.UTF_8));

            if (msg instanceof LastHttpContent) {
                final Request request = new Request(ctx, req);

                final String contentType = req.headers().get(HttpHeaderNames.CONTENT_TYPE);
                switch (contentType) {
                    case "application/json":
                        final JSONObject json = new JSONObject(buf.toString());
                        request.setJSON(json);
                        break;
                    case "application/x-www-form-urlencoded":
                        final QueryStringDecoder queryDecoder = new QueryStringDecoder(content.toString(CharsetUtil.UTF_8), false);
                        final Map<String, List<String>> uriAttributes = queryDecoder.parameters();
                        for (final Map.Entry<String, List<String>> p : uriAttributes.entrySet()) {
                            final String key = p.getKey();
                            final List<String> vals = p.getValue();
                            for (String val : vals) {
                                request.setParameter(key, val);
                            }
                        }
                        break;
                    case "multipart/form-data":
                        // TODO: 文件上传
                        LOGGER.log(Level.WARN, "TODO: handle file upload");
                        break;
                }

                final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                final Response response = new Response(ctx, req, res);
                Dispatcher.handle(request, response);
            }
        }
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