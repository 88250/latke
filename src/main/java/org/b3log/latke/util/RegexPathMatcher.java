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

import java.util.regex.Pattern;

/**
 * Regular expression path matcher.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 31, 2011
 * @see AntPathMatcher
 */
public final class RegexPathMatcher {

    /**
     * Determines whether the specified path matches the specified regular 
     * expression pattern.
     * 
     * @param pattern the specified regular expression pattern
     * @param path the specified path
     * @return {@code true} if matches, returns {@code false} otherwise
     */
    public static boolean match(final String pattern, final String path) {
        final Pattern p = Pattern.compile(pattern);

        return p.matcher(path).matches();
    }

    /**
     * Private constructor.
     */
    private RegexPathMatcher() {
    }
}
