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

import io.netty.handler.codec.http.cookie.DefaultCookie;

/**
 * HTTP cookie.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 14, 2020
 * @since 3.0.0
 */
public class Cookie {

    io.netty.handler.codec.http.cookie.Cookie cookie;

    public Cookie(final String name, final String value) {
        cookie = new DefaultCookie(name, value);
    }

    public Cookie(final io.netty.handler.codec.http.cookie.Cookie cookie) {
        this.cookie = cookie;
    }

    public String getName() {
        return cookie.name();
    }

    public String getValue() {
        return cookie.value();
    }

    public void setMaxAge(final long maxAge) {
        cookie.setMaxAge(maxAge);
    }

    public void setPath(final String path) {
        cookie.setPath(path);
    }

    public String getPath() {
        return cookie.path();
    }

    public void setHttpOnly(final boolean httpOnly) {
        cookie.setHttpOnly(httpOnly);
    }

    public void setSecure(boolean secure) {
        cookie.setSecure(secure);
    }

    public void setSameSite(String value) {
        // TODO: 等待 Netty 支持
    }
}
