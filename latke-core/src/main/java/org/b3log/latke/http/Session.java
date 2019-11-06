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

    String id;

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
