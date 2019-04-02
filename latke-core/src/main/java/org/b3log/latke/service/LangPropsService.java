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
package org.b3log.latke.service;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Locales;

import java.util.*;

/**
 * Language service implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.3, Dec 28, 2018
 * @since 2.4.18
 */
@Singleton
public class LangPropsService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LangPropsService.class);

    /**
     * Language properties.
     */
    private static final Map<Locale, Map<String, String>> LANGS = new HashMap<>();

    /**
     * Gets all language properties as a map by the specified locale.
     *
     * @param locale the specified locale
     * @return a map of language configurations
     */
    public Map<String, String> getAll(final Locale locale) {
        Map<String, String> ret = LANGS.get(locale);
        if (null == ret) {
            ret = new HashMap<>();
            final ResourceBundle defaultLangBundle = ResourceBundle.getBundle(Keys.LANGUAGE, Latkes.getLocale());
            final Enumeration<String> defaultLangKeys = defaultLangBundle.getKeys();
            while (defaultLangKeys.hasMoreElements()) {
                final String key = defaultLangKeys.nextElement();
                final String value = replaceVars(defaultLangBundle.getString(key));

                ret.put(key, value);
            }

            final ResourceBundle langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
            final Enumeration<String> langKeys = langBundle.getKeys();
            while (langKeys.hasMoreElements()) {
                final String key = langKeys.nextElement();
                final String value = replaceVars(langBundle.getString(key));

                ret.put(key, value);
            }

            LANGS.put(locale, ret);
        }

        return ret;
    }

    /**
     * Gets a value from {@link org.b3log.latke.Latkes#getLocale() the current locale} specified language properties
     * file with the specified key.
     *
     * @param key the specified key
     * @return value
     */
    public String get(final String key) {
        return get(Keys.LANGUAGE, key, Locales.getLocale());
    }

    /**
     * Gets a value with the specified key and locale.
     *
     * @param key    the specified key
     * @param locale the specified locale
     * @return value
     */
    public String get(final String key, final Locale locale) {
        return get(Keys.LANGUAGE, key, locale);
    }

    /**
     * Gets a value from baseName_locale.properties file with the specified key. If not found
     * baseName_(locale).properties configurations, using {@link Latkes#getLocale()} instead.
     *
     * @param baseName base name of resource bundle, options as the following:
     *                 <ul>
     *                 <li>{@link Keys#LANGUAGE}</li>
     *                 </ul>
     * @param key      the specified key
     * @param locale   the specified locale
     * @return the value of the specified key
     */
    private String get(final String baseName, final String key, final Locale locale) {
        if (!Keys.LANGUAGE.equals(baseName)) {
            final RuntimeException e = new RuntimeException("i18n resource [baseName=" + baseName + "] not found");

            LOGGER.log(Level.ERROR, e.getMessage(), e);

            throw e;
        }

        try {
            return replaceVars(ResourceBundle.getBundle(baseName, locale).getString(key));
        } catch (final MissingResourceException e) {
            LOGGER.log(Level.WARN, "{0}, get it from default locale [{1}]", e.getMessage(), Latkes.getLocale());

            return replaceVars(ResourceBundle.getBundle(baseName, Latkes.getLocale()).getString(key));
        }
    }

    /**
     * Replaces all variables of the specified language value.
     *
     * <p>
     * Variables:
     * <ul>
     * <li>${servePath}</li>
     * <li>${staticServePath}</li>
     * </ul>
     * </p>
     *
     * @param langValue the specified language value
     * @return replaced value
     */
    private String replaceVars(final String langValue) {
        String ret = StringUtils.replace(langValue, "${servePath}", Latkes.getServePath());
        ret = StringUtils.replace(ret, "${staticServePath}", Latkes.getStaticServePath());

        return ret;
    }
}
