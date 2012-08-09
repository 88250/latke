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
package org.b3log.latke.urlfetch.local;

import org.b3log.latke.servlet.HTTPRequestMethod;

/**
 * UrlFetchHandlerFactory to get  UrlFetchHandler.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 0.0.0.3, Aug 21, 2011
 * 
 */
final class UrlFetchHandlerFactory {

    /**
     * 
     * @param requestMethod {@link HTTPRequestMethod}
     * @return {@link UrlFetchCommonHandler}
     */
    public static UrlFetchCommonHandler getFetchHandler(
            final HTTPRequestMethod requestMethod) {
        UrlFetchCommonHandler ret = null;

        /*
         * now just Distinguish POST and the others.
         */
        switch (requestMethod) {
            case POST:
                ret = new UrlFetchPostHandler();
                break;
            default:
                ret = new UrlFetchCommonHandler();
                break;

        }

        return ret;
    }

    /**
     * Private construction method.
     */
    private UrlFetchHandlerFactory() {
    }
}
