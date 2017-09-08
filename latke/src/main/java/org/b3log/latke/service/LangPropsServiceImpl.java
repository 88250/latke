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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Locales;

/**
 * Language service implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.1, Oct 27, 2016
 */
@Named
@Singleton
public class LangPropsServiceImpl implements LangPropsService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LangPropsServiceImpl.class);

    /**
     * Language properties.
     */
    private static final Map<Locale, Map<String, String>> LANGS = new HashMap<Locale, Map<String, String>>();

    @Override
    public Map<String, String> getAll(final Locale locale) {
        Map<String, String> ret = LANGS.get(locale);

        if (null == ret) {
            ret = new HashMap<>();
            ResourceBundle langBundle;

            try {
                langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
            } catch (final MissingResourceException e) {
                LOGGER.log(Level.WARN, "{0}, using default locale[{1}] instead", new Object[]{e.getMessage(), Latkes.getLocale()});

                try {
                    langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, Latkes.getLocale());
                } catch (final MissingResourceException ex) {
                    LOGGER.log(Level.WARN, "{0}, using default lang.properties instead", new Object[]{e.getMessage()});
                    langBundle = ResourceBundle.getBundle(Keys.LANGUAGE);
                }
            }

            final Enumeration<String> keys = langBundle.getKeys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                final String value = replaceVars(langBundle.getString(key));

                ret.put(key, value);
            }

            LANGS.put(locale, ret);
        }

        return ret;
    }

    @Override
    public String get(final String key) {
        return get(Keys.LANGUAGE, key, Locales.getLocale());
    }

    @Override
    public String get(final String key, final Locale locale) {
        return get(Keys.LANGUAGE, key, locale);
    }

    /**
     * Gets a value from baseName_locale.properties file with the specified key. If not found
     * baseName_(locale).properties configurations, using {@link Latkes#getLocale()} instead.
     *
     * @param baseName base name of resource bundle, options as the following:
     * <ul>
     * <li>{@link Keys#LANGUAGE}</li>
     * </ul>
     * @param key the specified key
     * @param locale the specified locale
     * @return the value of the specified key
     */
    private String get(final String baseName, final String key, final Locale locale) {
        if (!Keys.LANGUAGE.equals(baseName)) {
            final RuntimeException e = new RuntimeException("i18n resource[baseName=" + baseName + "] not found");

            LOGGER.log(Level.ERROR, e.getMessage(), e);

            throw e;
        }

        try {
            return replaceVars(ResourceBundle.getBundle(baseName, locale).getString(key));
        } catch (final MissingResourceException e) {
            LOGGER.log(Level.WARN, "{0}, get it from default locale[{1}]", new Object[]{e.getMessage(), Latkes.getLocale()});

            return ResourceBundle.getBundle(baseName, Latkes.getLocale()).getString(key);
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
