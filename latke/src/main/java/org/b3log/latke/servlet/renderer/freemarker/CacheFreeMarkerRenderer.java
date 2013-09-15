/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.servlet.renderer.freemarker;


import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.util.Strings;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;


/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response renderer.
 * 
 * <p>
 * This renderer will put page content into cache.
 * <p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, May 5, 2012
 */
public class CacheFreeMarkerRenderer extends AbstractFreeMarkerRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CacheFreeMarkerRenderer.class.getName());

    @Override
    protected void beforeRender(final HTTPRequestContext context) throws Exception {}

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Processes page caching.
     * </p>
     */
    @Override
    protected void afterRender(final HTTPRequestContext context) throws Exception {
        final HttpServletRequest request = context.getRequest();
        final String pageContent = (String) request.getAttribute(PageCaches.CACHED_CONTENT);

        if (null == pageContent) {
            return;
        }

        if (Latkes.isPageCacheEnabled()) {
            final String cachedPageKey = (String) request.getAttribute(Keys.PAGE_CACHE_KEY);

            if (Strings.isEmptyOrNull(cachedPageKey)) {
                return;
            }

            LOGGER.log(Level.TRACE, "Caching page[cachedPageKey={0}]", cachedPageKey);

            check(request, pageContent);

            final JSONObject cachedValue = new JSONObject();

            cachedValue.put(PageCaches.CACHED_CONTENT, pageContent);
            cachedValue.put(PageCaches.CACHED_TYPE, request.getAttribute(PageCaches.CACHED_TYPE));
            cachedValue.put(PageCaches.CACHED_OID, request.getAttribute(PageCaches.CACHED_OID));
            cachedValue.put(PageCaches.CACHED_TITLE, request.getAttribute(PageCaches.CACHED_TITLE));
            cachedValue.put(PageCaches.CACHED_LINK, request.getAttribute(PageCaches.CACHED_LINK));
            if (null != request.getAttribute(PageCaches.CACHED_PWD)) {
                cachedValue.put(PageCaches.CACHED_PWD, request.getAttribute(PageCaches.CACHED_PWD));
            }

            PageCaches.put(cachedPageKey, cachedValue, request);
            LOGGER.log(Level.TRACE, "Cached page[cachedPageKey={0}]", cachedPageKey);
        }
    }

    /**
     * Checks if all conditions for caching page are ready by the specified 
     * request and content.
     * 
     * @param request the specified request
     * @param content the specified content
     */
    public static void check(final HttpServletRequest request, final String content) {
        if (Strings.isEmptyOrNull(content) || Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_TYPE))
            || Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_OID))
            || Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_TITLE))
            || Strings.isEmptyOrNull((String) request.getAttribute(PageCaches.CACHED_LINK))) {
            throw new IllegalArgumentException("Illegal arguments for caching page, " + "resolve this bug first!");
        }
    }
}
