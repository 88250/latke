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
package org.b3log.latke.ioc.mock;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * A mock servlet context for test mainly.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.3, Apr 27, 2014
 */
public final class MockServletContext implements ServletContext {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MockServletContext.class);

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
                webappRoot = new File(getClass().getResource("/").toURI());
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Unable to find web.xml, ignores this exception if you are running with a servlet container", e);
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

    @Override
    public int getEffectiveMajorVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getEffectiveMinorVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setInitParameter(final String name, final String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(final String servletName, final String className) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Servlet> T createServlet(final Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletRegistration getServletRegistration(final String servletName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(final String filterName, final String className) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Filter> T createFilter(final Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FilterRegistration getFilterRegistration(final String filterName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(final String className) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends EventListener> void addListener(final T t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addListener(final Class<? extends EventListener> listenerClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void declareRoles(final String... roleNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVirtualServerName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
