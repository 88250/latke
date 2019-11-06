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

    class Message {
        public String text;
        public WebSocketSession session;

        public Message(final String text, final WebSocketSession session) {
            this.text = text;
            this.session = session;
        }
    }
}
