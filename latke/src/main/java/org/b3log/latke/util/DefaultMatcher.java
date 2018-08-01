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

import org.weborganic.furi.URIPattern;
import org.weborganic.furi.URIResolveResult;
import org.weborganic.furi.URIResolver;

/**
 * the url-match util.
 * <p>
 * using https://code.google.com/p/wo-furi/ which is Deprecated.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
public final class DefaultMatcher {

    /**
     * Do match.
     *
     * @param pattern     pattern uri
     * @param requestPath request uri
     * @return {@link URIResolveResult}
     */
    public static URIResolveResult match(final String pattern, final String requestPath) {
        final URIResolver uriResolver = new URIResolver(requestPath);
        final URIPattern uriPattern = new URIPattern(pattern);

        return uriResolver.resolve(uriPattern);
    }

    /**
     * Private constructor.
     */
    private DefaultMatcher() {
    }
}
