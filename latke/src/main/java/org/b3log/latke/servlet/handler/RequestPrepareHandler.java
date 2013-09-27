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
package org.b3log.latke.servlet.handler;


import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.util.Strings;


/**
 * HTTP request prepare handler. This handler will set the following attributes for the current request: 
 * 
 * <ul>
 *   <li>startTimeMillis</li>Current time millisecond.
 *   <li>pageCacheKey</li>Generated page cache key if {@link Latkes#isPageCacheEnabled() page cache nabled}.
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Sep 27, 2013
 */
public class RequestPrepareHandler implements Ihandler {

    @Override
    public void handle(final HTTPRequestContext context, final HttpControl httpControl) throws Exception {
        final HttpServletRequest request = context.getRequest();

        final long startTimeMillis = System.currentTimeMillis();

        request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);

        if (Latkes.isPageCacheEnabled()) {
            final String queryString = request.getQueryString();
            String pageCacheKey = (String) request.getAttribute(Keys.PAGE_CACHE_KEY);

            if (Strings.isEmptyOrNull(pageCacheKey)) {
                pageCacheKey = PageCaches.getPageCacheKey(request.getRequestURI(), queryString);
                request.setAttribute(Keys.PAGE_CACHE_KEY, pageCacheKey);
            }
        }
    }
}
