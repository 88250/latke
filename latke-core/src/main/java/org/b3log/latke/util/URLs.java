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
package org.b3log.latke.util;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

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
    private static final Logger LOGGER = Logger.getLogger(URLs.class);

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
