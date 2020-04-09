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
package org.b3log.latke.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * URL utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Mar 17, 2019
 * @since 2.4.4
 */
public final class URLs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(URLs.class);

    /**
     * Encodes the specified string.
     *
     * @param str the specified string
     * @return URL encoded string
     */
    public static String encode(final String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Encodes str [" + str + "] failed", e);

            return str;
        }
    }

    /**
     * Decodes the specified string.
     *
     * @param str the specified string
     * @return URL decoded string
     */
    public static String decode(final String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Decodes str [" + str + "] failed", e);

            return str;
        }
    }

    /**
     * Private constructor.
     */
    private URLs() {
    }
}
