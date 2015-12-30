/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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
import javax.inject.Named;
import javax.inject.Singleton;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.model.Label;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 8, 2013
 */
@Named
@Singleton
public class LangPropsServiceImpl implements LangPropsService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LangPropsServiceImpl.class.getName());

    /**
     * Language properties.
     */
    private static final Map<Locale, Map<String, String>> LANGS = new HashMap<Locale, Map<String, String>>();

    @Override
    public Map<String, String> getAll(final Locale locale) {
        Map<String, String> ret = LANGS.get(locale);

        if (null == ret) {
            ret = new HashMap<String, String>();
            ResourceBundle langBundle;

            try {
                langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
            } catch (final MissingResourceException e) {
                LOGGER.log(Level.WARN, "{0}, using default locale[{1}] instead", new Object[] {e.getMessage(), Latkes.getLocale()});

                try {
                    langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, Latkes.getLocale());
                } catch (final MissingResourceException ex) {
                    LOGGER.log(Level.WARN, "{0}, using default lang.properties instead", new Object[] {e.getMessage()});
                    langBundle = ResourceBundle.getBundle(Keys.LANGUAGE);
                }
            }

            final Enumeration<String> keys = langBundle.getKeys();

            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                final String value = langBundle.getString(key);

                ret.put(key, value);
            }

            LANGS.put(locale, ret);
        }

        return ret;
    }

    @Override
    public JSONObject getLabels(final Locale locale) {
        final JSONObject ret = new JSONObject();
        ResourceBundle langBundle;

        try {
            langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
        } catch (final MissingResourceException e) {
            LOGGER.log(Level.WARN, "{0}, using default locale[{1}]  instead", new Object[] {e.getMessage(), Latkes.getLocale()});

            langBundle = ResourceBundle.getBundle(Keys.LANGUAGE, Latkes.getLocale());
        }

        final Enumeration<String> keys = langBundle.getKeys();
        final JSONArray labels = new JSONArray();

        ret.put(Label.LABELS, labels);

        while (keys.hasMoreElements()) {
            final JSONObject label = new JSONObject();
            final String key = keys.nextElement();

            label.put(Label.LABEL_ID, key);
            label.put(Label.LABEL_TEXT, langBundle.getString(key));

            labels.put(label);
        }

        return ret;
    }

    @Override
    public String get(final String key) {
        return get(Keys.LANGUAGE, key, Latkes.getLocale());
    }

    /**
     * Gets a value from baseName_locale.properties file with the specified key.
     * If not found baseName_(locale).properties configurations, using
     * {@link Latkes#getLocale()} instead.
     *
     * @param baseName base name of resource bundle, options as the following:
     * <ul>
     *   <li>{@link Keys#LANGUAGE}</li>
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
            return ResourceBundle.getBundle(baseName, locale).getString(key);
        } catch (final MissingResourceException e) {
            LOGGER.log(Level.WARN, "{0}, get it from default locale[{1}]", new Object[] {e.getMessage(), Latkes.getLocale()});

            return ResourceBundle.getBundle(baseName, Latkes.getLocale()).getString(key);
        }
    }
}
