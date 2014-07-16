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


import org.b3log.latke.Latkes;
import org.b3log.latke.cron.CronService;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.config.Discoverer;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.jdbc.JdbcRepository;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Collection;
import java.util.Locale;
import org.b3log.latke.ioc.mock.MockServletContext;


/**
 * Abstract servlet listener.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.5.3, Apr 15, 2014
 */
public abstract class AbstractServletListener implements ServletContextListener, ServletRequestListener, HttpSessionListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractServletListener.class.getName());

    /**
     * Servlet context.
     */
    private static ServletContext servletContext;

    static {
        servletContext = new MockServletContext();
    }

    /**
     * Initializes context, locale and runtime environment.
     *
     * @param servletContextEvent servlet context event
     */
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        servletContext = servletContextEvent.getServletContext();
        
        Latkes.initRuntimeEnv();
        LOGGER.info("Initializing the context....");

        Latkes.setLocale(Locale.SIMPLIFIED_CHINESE);
        LOGGER.log(Level.INFO, "Default locale [{0}]", Latkes.getLocale());

        final String realPath = servletContext.getRealPath("/");

        LOGGER.log(Level.INFO, "Server [realPath={0}, contextPath={1}]", new Object[] {realPath, servletContext.getContextPath()});

        try {
            final Collection<Class<?>> beanClasses = Discoverer.discover(Latkes.getScanPath());

            Lifecycle.startApplication(beanClasses); // Starts Latke IoC container
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Initializes request processors failed", e);

            throw new IllegalStateException("Initializes request processors failed");
        }

        CronService.start();
    }

    /**
     * Destroys the context, unregisters remote JavaScript services.
     *
     * @param servletContextEvent servlet context event
     */
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        LOGGER.info("Destroying the context....");
        Latkes.shutdown();
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        if (Latkes.runsWithJDBCDatabase()) {
            JdbcRepository.dispose();
        }
    }

    @Override
    public abstract void requestInitialized(final ServletRequestEvent servletRequestEvent);

    @Override
    public abstract void sessionCreated(final HttpSessionEvent httpSessionEvent);

    @Override
    public abstract void sessionDestroyed(final HttpSessionEvent httpSessionEvent);

    /**
     * Gets the servlet context.
     *
     * @return the servlet context
     */
    public static ServletContext getServletContext() {
        if (null == servletContext) {
            throw new IllegalStateException("Initializes the servlet context first!");
        }

        return servletContext;
    }
}
