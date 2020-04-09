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

/**
 * Websocket channel.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 6, 2019
 * @since 3.0.2
 */
public interface WebSocketChannel {

    void onConnect(final WebSocketSession session);

    void onMessage(final Message message);

    void onClose(final WebSocketSession session);

    void onError(final Error error);

    class Message {
        public String text;
        public WebSocketSession session;

        Message(final String text, final WebSocketSession session) {
            this.text = text;
            this.session = session;
        }
    }

    class Error {
        public Throwable cause;
        public WebSocketSession session;

        Error(final Throwable cause, final WebSocketSession session) {
            this.cause = cause;
            this.session = session;
        }
    }
}
