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

import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.Keys;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.util.Strings;
import java.util.logging.Level;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Latkes;
import org.json.JSONException;
import org.json.JSONObject;
import org.b3log.latke.servlet.renderer.HTTP404Renderer;
import org.b3log.latke.util.StaticResources;
import org.b3log.latke.util.Stopwatchs;

/**
 * Front controller for HTTP request dispatching.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.9, May 11, 2012
 */
public final class HTTPRequestDispatcher extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(HTTPRequestDispatcher.class.getName());
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
     * Current default servlet name.
     */
    private String defaultServletName;

    /**
     * Initializes this servlet.
     * 
     * <p>
     * Scans classpath for discovering request processors, configured the 'default' servlet for static resource processing.
     * </p>
     * 
     * @throws ServletException servlet exception
     * @see RequestProcessors#discover() 
     */
    @Override
    public void init() throws ServletException {
        Stopwatchs.start("Discovering Request Processors");
        try {
            LOGGER.info("Discovering request processors....");
            RequestProcessors.discover();
            LOGGER.info("Discovered request processors");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Initializes request processors failed", e);
        } finally {
            Stopwatchs.end();
        }

        final ServletContext servletContext = getServletContext();
        if (servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
            defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
        } else if (servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
            defaultServletName = GAE_DEFAULT_SERVLET_NAME;
        } else if (servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
            defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
        } else if (servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
            defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
        } else if (servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
            defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
        } else {
            throw new IllegalStateException("Unable to locate the default servlet for serving static content. "
                                            + "Please set the 'defaultServletName' property explicitly.");
            // TODO: Loads from local.properties 
        }

        LOGGER.log(Level.CONFIG, "The default servlet for serving static resource is [{0}]", defaultServletName);
    }

    /**
     * Serves.
     *
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String resourcePath = request.getPathTranslated();
        final String requestURI = request.getRequestURI();

        LOGGER.log(Level.FINEST, "Request[contextPath={0}, pathTranslated={1}, requestURI={2}]",
                   new Object[]{request.getContextPath(), resourcePath, requestURI});

        if (StaticResources.isStatic(request)) {
            final RequestDispatcher requestDispatcher = getServletContext().getNamedDispatcher(defaultServletName);
            if (null == requestDispatcher) {
                throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet ["
                                                + this.defaultServletName + "]");
            }

            requestDispatcher.forward(request, response);
            return;
        }

        final long startTimeMillis = System.currentTimeMillis();
        request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);

        if (Latkes.isPageCacheEnabled()) {
            final String queryString = request.getQueryString();
            String pageCacheKey = (String) request.getAttribute(Keys.PAGE_CACHE_KEY);
            if (Strings.isEmptyOrNull(pageCacheKey)) {
                pageCacheKey = PageCaches.getPageCacheKey(requestURI, queryString);
                request.setAttribute(Keys.PAGE_CACHE_KEY, pageCacheKey);
            }
        }

        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");

        final HTTPRequestContext context = new HTTPRequestContext();
        context.setRequest(request);
        context.setResponse(response);

        dispatch(context);
    }

    /**
     * Dispatches with the specified context.
     * 
     * @param context the specified specified context
     * @throws ServletException servlet exception
     * @throws IOException io exception 
     */
    public static void dispatch(final HTTPRequestContext context) throws ServletException, IOException {
        final HttpServletRequest request = context.getRequest();

        String requestURI = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);
        if (Strings.isEmptyOrNull(requestURI)) {
            requestURI = request.getRequestURI();
        }

        String method = (String) request.getAttribute(Keys.HttpRequest.REQUEST_METHOD);
        if (Strings.isEmptyOrNull(method)) {
            method = request.getMethod();
        }

        LOGGER.log(Level.FINER, "Request[requestURI={0}, method={1}]", new Object[]{requestURI, method});

        try {
            final Object processorMethodRet = RequestProcessors.invoke(requestURI, Latkes.getContextPath(), method, context);
        } catch (final Exception e) {
            final String exceptionTypeName = e.getClass().getName();
            LOGGER.log(Level.FINER,
                       "Occured error while processing request[requestURI={0}, method={1}, exceptionTypeName={2}, errorMsg={3}]",
                       new Object[]{requestURI, method, exceptionTypeName, e.getMessage()});
            if ("com.google.apphosting.api.ApiProxy$OverQuotaException".equals(exceptionTypeName)) {
                PageCaches.removeAll();

                context.getResponse().sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            }

            throw new ServletException(e);
        } catch (final Error e) {
            final Runtime runtime = Runtime.getRuntime();
            LOGGER.log(Level.FINER, "Memory status[total={0}, max={1}, free={2}]",
                       new Object[]{runtime.totalMemory(), runtime.maxMemory(), runtime.freeMemory()});

            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw e;
        }

        // XXX: processor method ret?

        final HttpServletResponse response = context.getResponse();
        if (response.isCommitted()) { // Sends rdirect or send error
            final PrintWriter writer = response.getWriter();
            writer.flush();
            writer.close();

            return;
        }

        AbstractHTTPResponseRenderer renderer = context.getRenderer();

        if (null == renderer) {
            renderer = new HTTP404Renderer();
        }

        renderer.render(context);
    }

    /**
     * Gets the query string(key1=value2&key2=value2&....) for the
     * specified HTTP servlet request.
     *
     * @param request the specified HTTP servlet request
     * @return a json object converts from query string, if can't convert the
     * query string, returns an empty json object;
     * @throws JSONException json exception
     */
    private JSONObject getQueryStringJSONObject(final HttpServletRequest request) throws JSONException {
        JSONObject ret = null;
        final String tmp = request.getQueryString();
        if (null == tmp) {
            return new JSONObject();
        }

        LOGGER.log(Level.FINEST, "Client is using QueryString[{0}]", tmp);
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        final String[] split = tmp.split("&");
        for (int i = 0; i < split.length; i++) {
            final String query = split[i];
            final String[] kv = query.split("=");
            if (kv.length != 2) {
                return new JSONObject();
            }

            final String key = kv[0];
            final String value = kv[1];
            sb.append("\"");
            sb.append(key);
            sb.append("\":");
            sb.append("\"");
            sb.append(value);
            sb.append("\"");
            if (i < split.length - 1) {
                sb.append(",");
            }
        }
        sb.append("}");

        ret = new JSONObject(sb.toString());

        return ret;
    }
}
