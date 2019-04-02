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
package org.b3log.latke.util;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Static resource utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.0, Mar 3, 2018
 */
public final class StaticResources {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticResources.class);

    /**
     * Static resource path patterns.
     * <p>
     * Initializes from file static-resources.xml.
     * </p>
     */
    private static final Set<String> STATIC_RESOURCE_PATHS = new TreeSet<>();

    /**
     * Determines whether the static resource path patterns has been initialized.
     */
    private static boolean inited;

    /**
     * Determines whether the client requests a static resource with the specified request.
     *
     * @param request the specified request
     * @return {@code true} if the client requests a static resource, returns {@code false} otherwise
     */
    public static boolean isStatic(final HttpServletRequest request) {
        final boolean requestStaticResourceChecked = null == request.getAttribute(Keys.HttpRequest.REQUEST_STATIC_RESOURCE_CHECKED)
                ? false
                : (Boolean) request.getAttribute(Keys.HttpRequest.REQUEST_STATIC_RESOURCE_CHECKED);

        if (requestStaticResourceChecked) {
            return (Boolean) request.getAttribute(Keys.HttpRequest.IS_REQUEST_STATIC_RESOURCE);
        }

        if (!inited) {
            init();
        }

        request.setAttribute(Keys.HttpRequest.REQUEST_STATIC_RESOURCE_CHECKED, true);
        request.setAttribute(Keys.HttpRequest.IS_REQUEST_STATIC_RESOURCE, false);

        final String requestURI = request.getRequestURI();

        for (final String pattern : STATIC_RESOURCE_PATHS) {
            if (AntPathMatcher.match(Latkes.getContextPath() + pattern, requestURI)) {
                request.setAttribute(Keys.HttpRequest.IS_REQUEST_STATIC_RESOURCE, true);
                return true;
            }
        }

        return false;
    }

    /**
     * Initializes the static resource path patterns.
     */
    private static synchronized void init() {
        LOGGER.trace("Reads static resources definition from [static-resources.xml]");

        final File staticResources = Latkes.getWebFile("/WEB-INF/static-resources.xml");
        if (null == staticResources || !staticResources.exists()) {
            throw new IllegalStateException("Not found static resources definition from [static-resources.xml]");
        }

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(staticResources);
            final Element root = document.getDocumentElement();

            root.normalize();

            final StringBuilder logBuilder = new StringBuilder("Reading static files: [").append(Strings.LINE_SEPARATOR);
            final NodeList includes = root.getElementsByTagName("include");
            for (int i = 0; i < includes.getLength(); i++) {
                final Element include = (Element) includes.item(i);
                String path = include.getAttribute("path");
                final URI uri = new URI("http", "b3log.org", path, null);
                final String s = uri.toASCIIString();
                path = StringUtils.substringAfter(s, "b3log.org");

                STATIC_RESOURCE_PATHS.add(path);

                logBuilder.append("    ").append("path pattern [").append(path).append("]");
                if (i < includes.getLength() - 1) {
                    logBuilder.append(",");
                }
                logBuilder.append(Strings.LINE_SEPARATOR);
            }

            logBuilder.append("]");

            if (LOGGER.isTraceEnabled()) {
                LOGGER.debug(logBuilder.toString());
            }
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Reads [" + staticResources.getName() + "] failed", e);
            throw new RuntimeException(e);
        }

        final StringBuilder logBuilder = new StringBuilder("Static files: [").append(Strings.LINE_SEPARATOR);
        final Iterator<String> iterator = STATIC_RESOURCE_PATHS.iterator();
        while (iterator.hasNext()) {
            final String pattern = iterator.next();

            logBuilder.append("    ").append(pattern);
            if (iterator.hasNext()) {
                logBuilder.append(',');
            }
            logBuilder.append(Strings.LINE_SEPARATOR);
        }
        logBuilder.append("], ").append('[').append(STATIC_RESOURCE_PATHS.size()).append("] path patterns");

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(logBuilder.toString());
        }

        inited = true;
    }

    /**
     * Private constructor.
     */
    private StaticResources() {
    }
}
