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

import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;

/**
 * HTTP cookie.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 19, 2020
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

    public void setValue(final String value) {
        cookie.setValue(value);
    }

    public String getValue() {
        return cookie.value();
    }

    public void setMaxAge(final long maxAge) {
        cookie.setMaxAge(maxAge);
    }

    public long getMaxAge() {
        return cookie.maxAge();
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

    public boolean isHttpOnly() {
        return cookie.isHttpOnly();
    }

    public void setSecure(boolean secure) {
        cookie.setSecure(secure);
    }

    public boolean isSecure() {
        return cookie.isSecure();
    }

    public void setSameSite(String value) {
        ((DefaultCookie) cookie).setSameSite(CookieHeaderNames.SameSite.valueOf(value));
    }

    public String getSameSite() {
        return ((DefaultCookie) cookie).sameSite().name();
    }

    public void setDomain(final String domain) {
        cookie.setDomain(domain);
    }

    public String getDomain() {
        return cookie.domain();
    }

    public void setWrap(final boolean b) {
        cookie.setWrap(b);
    }

    public boolean isWrap() {
        return cookie.wrap();
    }
}
