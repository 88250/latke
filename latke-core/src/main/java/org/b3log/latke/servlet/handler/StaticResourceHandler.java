/*
 * Copyright (c) 2009-present, b3log.org
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

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.renderer.StaticFileRenderer;
import org.b3log.latke.util.StaticResources;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Static resource handler.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Mar 3, 2018
 */
public class StaticResourceHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticResourceHandler.class);

    /**
     * Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish.
     */
    private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

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
    private static final String[] OPTION_SERVLET_NAME = new String[]{
            COMMON_DEFAULT_SERVLET_NAME, RESIN_DEFAULT_SERVLET_NAME, WEBLOGIC_DEFAULT_SERVLET_NAME,
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
     * Public construct with specified servlet context.
     *
     * @param servletContext the specified servlet context
     */
    public StaticResourceHandler(final ServletContext servletContext) {
        for (final String servletName : OPTION_SERVLET_NAME) {
            requestDispatcher = servletContext.getNamedDispatcher(servletName);
            if (null != requestDispatcher) {
                defaultServletName = servletName;

                break;
            }
        }

        if (null == requestDispatcher) {
            throw new IllegalStateException(
                    "Unable to locate the default servlet for serving static content. "
                            + "Please report this issue on https://github.com/b3log/latke/issues/new");
        }

        LOGGER.log(Level.DEBUG, "The default servlet for serving static resource is [{0}]", defaultServletName);
    }

    @Override
    public void handle(final RequestContext context) {
        final HttpServletRequest request = context.getRequest();
        if (StaticResources.isStatic(request)) {
            if (null == requestDispatcher) {
                throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet ["
                        + this.defaultServletName + "]");
            }

            context.setRenderer(new StaticFileRenderer(requestDispatcher));
            context.abort();

            return;
        }

        context.handle();
    }
}
