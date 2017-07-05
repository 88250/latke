/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.service;

import java.util.Locale;
import java.util.Map;

/**
 * Language service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 3.1.0.0, Oct 27, 2016
 */
public interface LangPropsService {

    /**
     * Gets a value from {@link org.b3log.latke.Latkes#getLocale() the current locale} specified language properties
     * file with the specified key.
     *
     * @param key the specified key
     * @return value
     */
    String get(final String key);

    /**
     * Gets a value with the specified key and locale.
     *
     * @param key the specified key
     * @param locale the specified locale
     * @return value
     */
    String get(final String key, final Locale locale);

    /**
     * Gets all language properties as a map by the specified locale.
     *
     * @param locale the specified locale
     * @return a map of language configurations
     */
    Map<String, String> getAll(final Locale locale);
}
