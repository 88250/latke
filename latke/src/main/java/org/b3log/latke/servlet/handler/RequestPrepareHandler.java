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
package org.b3log.latke.servlet.handler;


import org.b3log.latke.Keys;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;

import javax.servlet.http.HttpServletRequest;


/**
 * HTTP request prepare handler. This handler will set the following attributes for the current request: 
 * 
 * <ul>
 *   <li>startTimeMillis</li>Current time millisecond.
 * </ul>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Sep 27, 2013
 */
public class RequestPrepareHandler implements Handler {

    @Override
    public void handle(final HTTPRequestContext context, final HttpControl httpControl) throws Exception {
        final HttpServletRequest request = context.getRequest();

        final long startTimeMillis = System.currentTimeMillis();

        request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);

        httpControl.nextHandler();
    }
}
