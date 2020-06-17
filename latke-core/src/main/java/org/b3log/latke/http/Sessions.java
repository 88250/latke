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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.json.JSONObject;

/**
 * HTTP session utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 3, 2019
 * @since 3.0.0
 */
public final class Sessions {

    private static final Logger LOGGER = LogManager.getLogger(Sessions.class);

    public static final Cache CACHE = CacheFactory.getCache("LATKE_SESSIONS");

    public static Session add() {
        if (!Latkes.isEnabledSession()) {
            LOGGER.log(Level.WARN, "Session management is disabled");

            return null;
        }

        final String sessionId = RandomStringUtils.randomAlphanumeric(16);
        final Session ret = new Session(sessionId);
        CACHE.put(sessionId, new JSONObject().put("id", sessionId));

        return ret;
    }

    public static boolean contains(final String sessionId) {
        return CACHE.contains(sessionId);
    }

    public static Session get(String sessionId) {
        final JSONObject session = CACHE.get(sessionId);
        if (null == session) {
            return null;
        }

        return new Session(session.optString("id"));
    }

    private Sessions() {
    }
}
