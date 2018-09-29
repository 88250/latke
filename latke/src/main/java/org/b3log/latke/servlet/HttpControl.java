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
package org.b3log.latke.servlet;


import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.handler.Handler;
import org.b3log.latke.util.Requests;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * the HttpControl for one request to do the data-stored and handler process.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 6, 2017
 */
public class HttpControl {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(HttpControl.class);

    /**
     * the constructor.
     *
     * @param handlerIterable    Iterator<Handler>
     * @param httpRequestContext HTTPRequestContext
     */
    public HttpControl(final Iterator<Handler> handlerIterable, final HTTPRequestContext httpRequestContext) {

        this.ihandlerIterable = handlerIterable;
        this.httpRequestContext = httpRequestContext;
    }

    /**
     * Iterator<Ihandler>.
     */
    private Iterator<Handler> ihandlerIterable;

    /**
     * HTTPRequestContext.
     */
    private HTTPRequestContext httpRequestContext;

    /**
     * the share-data in one request.
     */
    private Map<String, Object> controlContext = new HashMap<String, Object>();

    /**
     * set the shared-data.
     *
     * @param key   key
     * @param value value
     */
    public void data(final String key, final Object value) {
        controlContext.put(key, value);
    }

    /**
     * get the shared-data.
     *
     * @param key key
     * @return value
     */
    public Object data(final String key) {
        return controlContext.get(key);
    }

    /**
     * nextHandler.
     */
    public void nextHandler() {
        if (ihandlerIterable.hasNext()) {
            try {
                ihandlerIterable.next().handle(httpRequestContext, this);
            } catch (final Exception e) {
                final HttpServletRequest request = httpRequestContext.getRequest();
                final String log = Requests.getLog(request);

                LOGGER.log(Level.ERROR, log + " processing failed ", e);
            }
        }
    }
}
