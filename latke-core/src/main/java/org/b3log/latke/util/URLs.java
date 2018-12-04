/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
 * @version 1.0.0.0, Aug 1, 2018
 * @since 2.4.4
 */
public final class URLs {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(URLs.class);

    /**
     * Encodes the specified URL.
     *
     * @param url the specified URL
     * @return encoded URL
     */
    public static String encode(final String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Encodes URL [" + url + "] failed", e);

            return url;
        }
    }

    /**
     * Decodes the specified URL.
     *
     * @param url the specified URL
     * @return decoded URL
     */
    public static String decode(final String url) {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Decodes URL [" + url + "] failed", e);

            return url;
        }
    }

    /**
     * Private constructor.
     */
    private URLs() {
    }
}
