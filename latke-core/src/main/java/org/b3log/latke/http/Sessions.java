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

import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HTTP session utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 3, 2019
 * @since 2.5.9
 */
public class Sessions {

    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public static Session add() {
        final String sessionId = RandomStringUtils.randomAlphanumeric(16);
        final Session ret = new Session(sessionId);
        sessionMap.put(sessionId, ret);
        return ret;
    }

    public static boolean contains(final String sessionId) {
        return sessionMap.containsKey(sessionId);
    }

    public static Session get(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
