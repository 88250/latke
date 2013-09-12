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
package org.b3log.latke.servlet;


import org.b3log.latke.servlet.handler.Ihandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午2:36
 */
public class HttpControl {

    public HttpControl(Iterator<Ihandler> ihandlerIterable, HTTPRequestContext httpRequestContext) {

        this.ihandlerIterable = ihandlerIterable;
        this.httpRequestContext = httpRequestContext;
    }

    private Iterator<Ihandler> ihandlerIterable;

    private HTTPRequestContext httpRequestContext;

    private Map<String, Object> controlContext = new HashMap<String, Object>();

    public void data(String key, String value) {
        controlContext.put(key, value);
    }

    public Object data(String key) {
        return controlContext.get(key);
    }

    public void nextHandler() {
        if (ihandlerIterable.hasNext()) {
            try {
                ihandlerIterable.next().handle(httpRequestContext, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
