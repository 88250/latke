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

import org.json.JSONObject;

/**
 * HTTP session.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 3, 2019
 * @since 3.0.0
 */
public class Session {

    public static final String LATKE_SESSION_ID = "LATKE_SESSION_ID";

    private final String id;

    public Session(final String id) {
        this.id = id;
    }

    public String getAttribute(final String name) {
        String ret = null;
        final JSONObject session = Sessions.CACHE.get(id);
        if (null == session) {
            return null;
        }

        if (session.has(name)) {
            ret = session.optString(name);
        }

        return ret;
    }

    public void setAttribute(final String name, final String value) {
        final JSONObject session = Sessions.CACHE.get(id);
        if (null == session) {
            return;
        }

        session.put(name, value);
        Sessions.CACHE.put(id, session);
    }

    public String getId() {
        return id;
    }
}
