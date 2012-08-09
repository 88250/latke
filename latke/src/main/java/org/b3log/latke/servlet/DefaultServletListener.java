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
package org.b3log.latke.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;

/**
 * Default servlet listener.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 28, 2011
 */
public final class DefaultServletListener extends AbstractServletListener {

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        super.requestDestroyed(servletRequestEvent);
    }

    @Override
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
    }

    @Override
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
    }
}
