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

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Sep 1, 2018
 */
public final class Strings {

    /**
     * Line separator.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Maximum length of local part of a valid email address.
     */
    private static final int MAX_EMAIL_LENGTH_LOCAL = 64;

    /**
     * Maximum length of domain part of a valid email address.
     */
    private static final int MAX_EMAIL_LENGTH_DOMAIN = 255;

    /**
     * Maximum length of a valid email address.
     */
    private static final int MAX_EMAIL_LENGTH = 256;

    /**
     * Email pattern.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    /**
     * Private constructor.
     */
    private Strings() {
    }

    /**
     * Is IPv4.
     *
     * @param ip ip
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public static boolean isIPv4(final String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }

        final String regex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(ip);

        return matcher.matches();
    }

    /**
     * Converts the specified string into a string list line by line.
     *
     * @param string the specified string
     * @return a list of string lines, returns {@code null} if the specified
     * string is {@code null}
     * @throws IOException io exception
     */
    public static List<String> toLines(final String string) throws IOException {
        if (null == string) {
            return null;
        }

        final List<String> ret = new ArrayList<>();
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(string))) {
            String line = bufferedReader.readLine();
            while (null != line) {
                ret.add(line);

                line = bufferedReader.readLine();
            }
        }

        return ret;
    }

    /**
     * Checks whether the specified string is numeric.
     *
     * @param string the specified string
     * @return {@code true} if the specified string is numeric, returns {@code false} otherwise
     */
    public static boolean isNumeric(final String string) {
        try {
            Double.parseDouble(string);
        } catch (final Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the specified string is an integer.
     *
     * @param string the specified string
     * @return {@code true} if the specified string is an integer, returns {@code false} otherwise
     */
    public static boolean isInteger(final String string) {
        try {
            Integer.parseInt(string);
        } catch (final Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the specified string is a valid email address.
     *
     * @param string the specified string
     * @return {@code true} if the specified string is a valid email address,
     * returns {@code false} otherwise
     */
    public static boolean isEmail(final String string) {
        if (StringUtils.isBlank(string)) {
            return false;
        }

        if (MAX_EMAIL_LENGTH < string.length()) {
            return false;
        }

        final String[] parts = string.split("@");

        if (2 != parts.length) {
            return false;
        }

        final String local = parts[0];

        if (MAX_EMAIL_LENGTH_LOCAL < local.length()) {
            return false;
        }

        final String domain = parts[1];

        if (MAX_EMAIL_LENGTH_DOMAIN < domain.length()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(string).matches();
    }

    /**
     * Trims every string in the specified strings array.
     *
     * @param strings the specified strings array, returns {@code null} if the
     *                specified strings is {@code null}
     * @return a trimmed strings array
     */
    public static String[] trimAll(final String[] strings) {
        if (null == strings) {
            return null;
        }

        return Arrays.stream(strings).map(StringUtils::trim).toArray(size -> new String[size]);
    }

    /**
     * Determines whether the specified strings contains the specified string, ignoring case considerations.
     *
     * @param string  the specified string
     * @param strings the specified strings
     * @return {@code true} if the specified strings contains the specified string, ignoring case considerations, returns {@code false}
     * otherwise
     */
    public static boolean containsIgnoreCase(final String string, final String[] strings) {
        if (null == strings) {
            return false;
        }

        return Arrays.stream(strings).anyMatch(str -> StringUtils.equalsIgnoreCase(string, str));
    }

    /**
     * Determines whether the specified strings contains the specified string.
     *
     * @param string  the specified string
     * @param strings the specified strings
     * @return {@code true} if the specified strings contains the specified string, returns {@code false} otherwise
     */
    public static boolean contains(final String string, final String[] strings) {
        if (null == strings) {
            return false;
        }

        return Arrays.stream(strings).anyMatch(str -> StringUtils.equals(string, str));
    }

    /**
     * Determines whether the specified string is a valid URL.
     *
     * @param string the specified string
     * @return {@code true} if the specified string is a valid URL, returns {@code false} otherwise
     */
    public static boolean isURL(final String string) {
        try {
            new URL(string);

            return true;
        } catch (final MalformedURLException e) {
            return false;
        }
    }
}
