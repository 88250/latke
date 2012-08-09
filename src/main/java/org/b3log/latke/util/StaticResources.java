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
package org.b3log.latke.util;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.servlet.AbstractServletListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Static resource utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, May 11, 2012
 */
public final class StaticResources {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticResources.class.getName());
    /**
     * Static resource path patterns.
     * 
     * <p>
     * Initializes from  file appengine-web.xml.
     * </p>
     */
    private static final Set<String> STATIC_RESOURCE_PATHS = new TreeSet<String>();

    static {
        final String webRoot = AbstractServletListener.getWebRoot();
        final File appengineWeb = new File(webRoot + File.separator + "WEB-INF" + File.separator + "appengine-web.xml");

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(appengineWeb);
            final Element root = document.getDocumentElement();
            root.normalize();

            final Element staticFiles = (Element) root.getElementsByTagName("static-files").item(0);
            final NodeList includes = staticFiles.getElementsByTagName("include");

            LOGGER.log(Level.CONFIG, "Reading static files: ");
            for (int i = 0; i < includes.getLength(); i++) {
                final Element include = (Element) includes.item(i);
                final String path = include.getAttribute("path");
                LOGGER.log(Level.CONFIG, "path pattern=[{0}]", path);
                STATIC_RESOURCE_PATHS.add(path);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Reads appengine-web.xml failed", e);
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

        LOGGER.log(Level.INFO, logBuilder.toString());
    }

    /**
     * Determines whether the client requests a static resource with the specified request.
     * 
     * @param request the specified request
     * @return {@code true} if the client requests a static resource, returns {@code false} otherwise
     */
    public static boolean isStatic(final HttpServletRequest request) {
        final boolean requestStaticResourceChecked =
                      null == request.getAttribute(Keys.HttpRequest.REQUEST_STATIC_RESOURCE_CHECKED)
                      ? false : (Boolean) request.getAttribute(Keys.HttpRequest.REQUEST_STATIC_RESOURCE_CHECKED);
        if (requestStaticResourceChecked) {
            return (Boolean) request.getAttribute(Keys.HttpRequest.IS_REQUEST_STATIC_RESOURCE);
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
     * Private constructor.
     */
    private StaticResources() {
    }
}
