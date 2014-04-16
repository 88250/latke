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
package org.b3log.latke.ioc.mock;


import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;


/**
 * A mock servlet context for test mainly.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Apr 16, 2014
 */
public final class MockServletContext implements ServletContext {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MockServletContext.class.getName());

    /**
     * Web root.
     */
    private File webappRoot;

    /**
     * WEB-INF.
     */
    private File webInfRoot;

    /**
     * WEB-INF/classes/.
     */
    private File webInfClassesRoot;

    /**
     * Constructs a mock servlet context.
     */
    public MockServletContext() {
        try {
            final URL webxml = getClass().getResource("/WEB-INF/web.xml");

            if (webxml != null) {
                webInfRoot = new File(webxml.toURI()).getParentFile();
                LOGGER.trace("WEB-INF: " + webInfRoot.getAbsolutePath());
                if (webInfRoot != null) {
                    webInfClassesRoot = new File(webInfRoot.getParentFile().getPath());
                    LOGGER.trace("WEB-INF/classes: " + webInfClassesRoot.getAbsolutePath());
                    webappRoot = webInfRoot.getParentFile();
                    LOGGER.trace("Web app root: " + webappRoot.getAbsolutePath());
                }
            } else {
                webappRoot = new File(getClass().getResource("/.").toURI());
            }
        } catch (final URISyntaxException e) {
            LOGGER.log(Level.WARN, "Unable to find web.xml", e);
        }
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public ServletContext getContext(final String uripath) {
        throw new UnsupportedOperationException("Not supported yet [getContext]");
    }

    @Override
    public int getMajorVersion() {
        throw new UnsupportedOperationException("Not supported yet [getMajorVersion]");
    }

    @Override
    public int getMinorVersion() {
        throw new UnsupportedOperationException("Not supported yet [getMinorVersion]");
    }

    @Override
    public String getMimeType(final String file) {
        throw new UnsupportedOperationException("Not supported yet [getMimeType]");
    }

    @Override
    public Set getResourcePaths(final String path) {
        return Collections.emptySet();
    }

    @Override
    public URL getResource(final String path) throws MalformedURLException {
        return getClass().getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(final String path) {
        return getClass().getResourceAsStream(path);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        throw new UnsupportedOperationException("Not supported yet [getRequestDispatcher]");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(final String name) {
        throw new UnsupportedOperationException("Not supported yet [getNamedDispatcher]");
    }

    @Override
    public Servlet getServlet(final String name) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet [getServlet]");
    }

    @Override
    public Enumeration getServlets() {
        throw new UnsupportedOperationException("Not supported yet [getServlets]");
    }

    @Override
    public Enumeration getServletNames() {
        throw new UnsupportedOperationException("Not supported yet [getServletNames]");
    }

    @Override
    public void log(final String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void log(final Exception exception, final String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void log(final String message, final Throwable throwable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRealPath(final String path) {
        if (webappRoot != null) {
            return webappRoot.getAbsolutePath() + path;
        } else {
            return path;
        }
    }

    @Override
    public String getServerInfo() {
        throw new UnsupportedOperationException("Not supported yet [getServerInfo]");
    }

    @Override
    public String getInitParameter(final String name) {
        throw new UnsupportedOperationException("Not supported yet [getInitParameter]");
    }

    @Override
    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException("Not supported yet [getInitParameterNames]");
    }

    @Override
    public Object getAttribute(final String name) {
        throw new UnsupportedOperationException("Not supported yet [getAttribute]");
    }

    @Override
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException("Not supported yet [getAttributeNames]");
    }

    @Override
    public void setAttribute(final String name, final Object object) {
        throw new UnsupportedOperationException("Not supported yet [setAttribute]");
    }

    @Override
    public void removeAttribute(final String name) {
        throw new UnsupportedOperationException("Not supported yet [removeAttribute]");
    }

    @Override
    public String getServletContextName() {
        return "mock servlet cocntext name";
    }
}
