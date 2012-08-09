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

import java.util.logging.Level;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.b3log.latke.Latkes;
import org.b3log.latke.cron.CronService;
import org.b3log.latke.repository.jdbc.JdbcRepository;

/**
 * Abstract servlet listener.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.0, Apr 5, 2012
 */
public abstract class AbstractServletListener implements ServletContextListener, ServletRequestListener, HttpSessionListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractServletListener.class.getName());
    /**
     * Web root.
     */
    private static String webRoot;

    static {
        final URL resource = ClassLoader.class.getResource("/");
        if (null != resource) { // Running unit tests
            webRoot = resource.getPath();
        }
    }

    /**
     * Initializes context, {@linkplain #webRoot web root}, locale and runtime environment.
     * 
     * @param servletContextEvent servlet context event
     */
    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Latkes.initRuntimeEnv();
        LOGGER.info("Initializing the context....");

        Latkes.setLocale(Locale.SIMPLIFIED_CHINESE);
        LOGGER.log(Level.INFO, "Default locale[{0}]", Latkes.getLocale());

        final ServletContext servletContext = servletContextEvent.getServletContext();
        webRoot = servletContext.getRealPath("") + File.separator;
        LOGGER.log(Level.INFO, "Server[webRoot={0}, contextPath={1}]",
                   new Object[]{webRoot, servletContextEvent.getServletContext().getContextPath()});

//        final String catalinaBase = System.getProperty("catalina.base");
//        if (!Strings.isEmptyOrNull(catalinaBase)) {
//            LOGGER.log(Level.INFO, "Servlet container[Tomcat, catalina.base={0}]", catalinaBase);
//        }

        CronService.start();
    }

    /**
     * Destroys the context, unregisters remote JavaScript services.
     * 
     * @param servletContextEvent
     *            servlet context event
     */
    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        LOGGER.info("Destroying the context....");
        Latkes.shutdown();
        // TODO: Stop cron jobs
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
     * Gets the absolute file path of web root directory on the server's file system.
     * 
     * @return the directory file path(tailing with {@link File#separator}).
     */
    public static String getWebRoot() {
        return webRoot;
    }
}
