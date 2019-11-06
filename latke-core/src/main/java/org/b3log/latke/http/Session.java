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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<String, String> attributes = new ConcurrentHashMap<>();

    public Session(final String id) {
        this.id = id;
    }

    public String getAttribute(final String name) {
        final JSONObject session = Sessions.CACHE.get(id);
        if (null != session) {
            final JSONArray attrs = session.optJSONArray("attrs");
            for (int i = 0; i < attrs.length(); i++) {
                final JSONObject attr = attrs.optJSONObject(i);
                if (attr.optString("n").equals(name)) {
                    return attr.optString("v");
                }
            }

        }

        return attributes.get(name);
    }

    public void setAttribute(final String name, final String value) {
        final JSONObject session = Sessions.CACHE.get(id);
        if (null != session) {
            final JSONArray attrs = session.optJSONArray("attrs");
            boolean exist = false;
            for (int i = 0; i < attrs.length(); i++) {
                final JSONObject attr = attrs.optJSONObject(i);
                if (attr.optString("n").equals(name)) {
                    attr.put(name, value);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                attrs.put(new JSONObject().put(name, value));
            }
            session.put("attrs", attrs);
            Sessions.CACHE.put(id, session);
        }

        attributes.put(name, value);
    }

    public String getId() {
        return id;
    }
}
