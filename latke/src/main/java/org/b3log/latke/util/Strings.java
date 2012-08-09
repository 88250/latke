/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, Dec 15, 2011
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
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    /**
     * Private default constructor.
     */
    private Strings() {
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

        final BufferedReader bufferedReader = new BufferedReader(new StringReader(string));
        final List<String> ret = new ArrayList<String>();

        try {
            String line = bufferedReader.readLine();
            while (null != line) {
                ret.add(line);

                line = bufferedReader.readLine();
            }
        } finally {
            bufferedReader.close();
        }

        return ret;
    }

    /**
     * Checks whether the specified string is numeric.
     * 
     * @param string the specified string
     * @return {@code true} if the specified string is numeric, returns 
     * returns {@code false} otherwise
     */
    public static boolean isNumeric(final String string) {
        if (isEmptyOrNull(string)) {
            return false;
        }

        final Pattern pattern = Pattern.compile("[0-9]*");
        final Matcher matcher = pattern.matcher(string);

        if (!matcher.matches()) {
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
        if (isEmptyOrNull(string)) {
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
     * Determines whether the specified string is {@code ""} or {@code null}.
     *
     * @param string the specified string
     * @return {@code true} if the specified string is {@code ""} or
     * {@code null}, returns {@code false} otherwise
     */
    public static boolean isEmptyOrNull(final String string) {
        return string == null || string.trim().length() == 0;
    }

    /**
     * Trims every string in the specified strings array.
     *
     * @param strings the specified strings array, returns {@code null} if the
     * specified strings is {@code null}
     * @return a trimmed strings array
     */
    public static String[] trimAll(final String[] strings) {
        if (null == strings) {
            return null;
        }

        final String[] ret = new String[strings.length];

        for (int i = 0; i < strings.length; i++) {
            ret[i] = strings[i].trim();
        }

        return ret;
    }

    /**
     * Determines whether the specified strings contains the specified string.
     * 
     * @param string the specified string
     * @param strings the specified strings
     * @return {@code true} if the specified strings contains the specified 
     * string, returns {@code false} otherwise
     */
    public static boolean contains(final String string, final String[] strings) {
        if (null == strings) {
            return false;
        }

        for (int i = 0; i < strings.length; i++) {
            final String str = strings[i];

            if (null == str && null == string) {
                return true;
            }

            if (null == string || null == str) {
                continue;
            }

            if (string.equals(str)) {
                return true;
            }
        }

        return false;
    }
}
