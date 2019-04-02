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
package org.b3log.latke.servlet.mock;

import org.b3log.latke.Latkes;
import org.b3log.latke.servlet.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * Mock HTTP servlet request.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, May 1, 2012
 */
public class MockHttpServletRequest implements HttpServletRequest {

    /**
     * Header.
     */
    private Map<String, String> headers = new HashMap<>();
    /**
     * Request URI.
     */
    private String requestURI = "/";
    /**
     * Context path.
     */
    private String contextPath = "";
    /**
     * Attributes.
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private Cookie[] cookies;

    public void setCookies(final Cookie[] cookies) {
        this.cookies = cookies;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public long getDateHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Sets header with the specified name and value.
     *
     * @param name  the specified name
     * @param value the specified value
     */
    public void setHeader(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(final String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration getHeaders(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Enumeration getHeaderNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getIntHeader(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    private String method = HttpMethod.GET.toString();

    public void setMethod(final String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isUserInRole(final String role) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Sets request URI with the specified request URI.
     *
     * @param requestURI the specified request URI
     */
    public void setRequestURI(final String requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(Latkes.getServePath() + requestURI);
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpSession getSession(final boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCharacterEncoding(final String env) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletInputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Map<String, String> param = new HashMap<>();

    public void putParameter(final String name, final String value) {
        param.put(name, value);
    }

    @Override
    public String getParameter(final String name) {
        return param.get(name);
    }

    @Override
    public Enumeration getParameterNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getParameterValues(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map getParameterMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private BufferedReader reader;

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public BufferedReader getReader() {
        return reader;
    }


    private String remoteAddr;

    public void setRemoteAddr(final String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttribute(final String name, final Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Enumeration getLocales() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRealPath(final String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean authenticate(final HttpServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void login(final String username, final String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void logout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<Part> getParts() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Part getPart(final String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getContentLengthLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}