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


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.renderer.StaticFileRenderer;
import org.b3log.latke.util.StaticResources;


/**
 * to handler static recources.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
public class StaticResourceHandler implements Ihandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticResourceHandler.class.getName());

    /**
     * Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish.
     */
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

    /**
     * Default Servlet name used by Google App Engine.
     */
    private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";

    /**
     * Default Servlet name used by Resin.
     */
    private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";

    /**
     * Default Servlet name used by WebLogic.
     */
    private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";

    /**
     * Default Servlet name used by WebSphere.
     */
    private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

    /**
     * the holder of All option Servlet Name.
     */
    private static final String[] OPTION_SERVLET_NAME = new String[] {
        COMMON_DEFAULT_SERVLET_NAME, GAE_DEFAULT_SERVLET_NAME, RESIN_DEFAULT_SERVLET_NAME, WEBLOGIC_DEFAULT_SERVLET_NAME,
        WEBSPHERE_DEFAULT_SERVLET_NAME};

    /**
     * default servlet which container provide to resolve static resource.
     */
    private RequestDispatcher requestDispatcher;

    /**
     * default-servlet name for logger.
     */
    private String defaultServletName;

    /**
     * the constructor.
     *
     * @param servletContext {@link ServletContext}
     */
    public StaticResourceHandler(final ServletContext servletContext) {

        for (String servletName : OPTION_SERVLET_NAME) {
            requestDispatcher = servletContext.getNamedDispatcher(servletName);
            if (requestDispatcher != null) {
                defaultServletName = servletName;
                break;
            }
        }
        if (requestDispatcher == null) {
            throw new IllegalStateException(
                "Unable to locate the default servlet for serving static content. "
                    + "Please report this bug on https://github.com/b3log/b3log-latke/issues/new");
        }

        LOGGER.log(Level.DEBUG, "The default servlet for serving static resource is [{0}]", defaultServletName);
    }

    @Override
    public void handle(final HTTPRequestContext context, final HttpControl httpControl) throws Exception {

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        if (StaticResources.isStatic(request)) {
            if (null == requestDispatcher) {
                throw new IllegalStateException(
                    "A RequestDispatcher could not be located for the default servlet [" + this.defaultServletName + "]");
            }
            
            context.setRenderer(new StaticFileRenderer(requestDispatcher));
           
        } else {
            httpControl.nextHandler();
        }
    }
}
