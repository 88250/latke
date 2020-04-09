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
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Latkes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Websocket session.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 6, 2019
 * @since 3.0.2
 */
public class WebSocketSession {

    String id;
    ChannelHandlerContext ctx;

    WebSocketChannel webSocketChannel;

    Session session;
    Set<Cookie> cookies = new HashSet<>();
    Map<String, String> params = new HashMap<>();

    WebSocketSession(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.id = RandomStringUtils.randomAlphanumeric(16);
    }

    public void sendText(final String text) {
        ctx.writeAndFlush(new TextWebSocketFrame(text));
    }

    public void close() {
        webSocketChannel.onClose(this);
        ctx.close();
    }

    public String getId() {
        return id;
    }

    public String getParameter(final String name) {
        return params.get(name);
    }

    public Session getHttpSession() {
        return session;
    }

    public Set<Cookie> getCookies() {
        return cookies;
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
}
