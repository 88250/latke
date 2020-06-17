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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.Session;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Locale utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Nov 5, 2019
 */
public final class Locales {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Locales.class);

    /**
     * Thread local holder for locale.
     */
    private static final ThreadLocal<Locale> LOCALE = new InheritableThreadLocal<>();

    /**
     * Language start description in Accept-Language of request header.
     */
    private static final int LANG_START = 0;

    /**
     * Language end description in Accept-Language of request header.
     */
    private static final int LANG_END = 2;

    /**
     * Country start description in Accept-Language of request header.
     */
    private static final int COUNTRY_START = 3;

    /**
     * Country end description in Accept-Language of request header.
     */
    private static final int COUNTRY_END = 5;

    /**
     * Private constructor.
     */
    private Locales() {
    }

    /**
     * Gets locale with the specified request.
     * <p>
     * By the following steps:
     * <ol>
     * <li>Gets from session of the specified request</li>
     * <li>Gets from the specified request header</li>
     * <li>Using {@link Latkes#getLocale() server configuration}</li>
     * </ol>
     *
     * @param request the specified request
     * @return locale
     */
    public static Locale getLocale(final Request request) {
        Locale locale = null;

        final Session session = request.getSession();
        if (session != null) {
            locale = getLocale(session.getAttribute(Keys.LOCALE));
        }

        if (null == locale) {
            // Gets from request header
            final String languageHeader = request.getHeader("Accept-Language");

            LOGGER.log(Level.DEBUG, "[Accept-Language={}]", languageHeader);

            String language = "zh";
            String country = "CN";

            if (StringUtils.isNotBlank(languageHeader)) {
                language = getLanguage(languageHeader);
                country = getCountry(languageHeader);
            }

            locale = new Locale(language, country);

            if (!hasLocale(locale)) {
                // Uses default
                locale = Latkes.getLocale();
                LOGGER.log(Level.DEBUG, "Using the default locale[{}]", locale.toString());
            } else {
                LOGGER.log(Level.DEBUG, "Got locale[{}] from request.", locale.toString());
            }
        } else {
            LOGGER.log(Level.DEBUG, "Got locale[{}] from session.", locale.toString());
        }

        return locale;
    }

    /**
     * Determines whether the server has the specified locale configuration or not.
     *
     * @param locale the specified locale
     * @return {@code true} if the server has the specified locale, {@code false} otherwise
     */
    public static boolean hasLocale(final Locale locale) {
        try {
            ResourceBundle.getBundle(Keys.LANGUAGE, locale);

            return true;
        } catch (final MissingResourceException e) {
            return false;
        }
    }

    /**
     * Sets the specified locale into session of the specified request.
     *
     * <p>
     * If no session of the specified request, do nothing.
     * </p>
     *
     * @param request the specified request
     * @param locale  a new locale
     */
    public static void setLocale(final Request request, final Locale locale) {
        final Session session = request.getSession();
        if (null == session) {
            LOGGER.warn("Ignores set locale caused by no session");
            return;
        }

        session.setAttribute(Keys.LOCALE, locale.toString());
        LOGGER.log(Level.DEBUG, "Client [sessionId={}] sets locale to [{}]", session.getId(), locale.toString());
    }

    /**
     * Sets locale.
     *
     * @param locale the specified locale
     */
    public static void setLocale(final Locale locale) {
        LOCALE.set(locale);
    }

    /**
     * Gets locale.
     *
     * @return locale
     */
    public static Locale getLocale() {
        final Locale ret = LOCALE.get();
        if (null == ret) {
            return Latkes.getLocale();
        }

        return ret;
    }

    /**
     * Gets country from the specified locale string.
     *
     * @param localeString the specified locale string
     * @return country, if the length of specified locale string less than 5, returns ""
     */
    public static String getCountry(final String localeString) {
        if (localeString.length() >= COUNTRY_END) {
            return localeString.substring(COUNTRY_START, COUNTRY_END);
        }

        return "";
    }

    /**
     * Gets language from the specified locale string.
     *
     * @param localeString the specified locale string
     * @return language, if the length of specified locale string less than 2, returns ""
     */
    public static String getLanguage(final String localeString) {
        if (localeString.length() >= LANG_END) {
            return localeString.substring(LANG_START, LANG_END);
        }

        return "";
    }

    /**
     * Get a {@link java.util.Locale} with the specified locale string.
     *
     * @param localeString the specified locale string
     * @return locale
     */
    public static Locale getLocale(final String localeString) {
        if (StringUtils.isBlank(localeString)) {
            return null;
        }

        return new Locale(getLanguage(localeString), getCountry(localeString));
    }
}
