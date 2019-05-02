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
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.json.JSONArray;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * Request utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:dongxv.vang@gmail.com">Dongxu Wang</a>
 * @version 2.1.0.0, May 2, 2019
 */
public final class Requests {

    /**
     * Cookie expiry of "visited".
     */
    private static final int COOKIE_EXPIRY = 60 * 60 * 24; // 24 hours

    /**
     * Logs the specified request with the specified level and logger.
     * <p>
     * Logging information of the specified request includes:
     * <ul>
     * <li>method</li>
     * <li>URL</li>
     * <li>content type</li>
     * <li>character encoding</li>
     * <li>local (address, port, name)</li>
     * <li>remote (address, port, host)</li>
     * <li>headers</li>
     * </ul>
     *
     * @param httpServletRequest the specified HTTP servlet request
     * @param level              the specified logging level
     * @param logger             the specified logger
     */
    public static void log(final HttpServletRequest httpServletRequest, final Level level, final Logger logger) {
        if (!logger.isLoggable(level)) {
            return;
        }

        logger.log(level, getLog(httpServletRequest));
    }

    /**
     * Gets log of the specified request.
     *
     * @param httpServletRequest the specified request
     * @return log
     */
    public static String getLog(final HttpServletRequest httpServletRequest) {
        if (null == httpServletRequest) {
            return "request is null";
        }

        final String indents = "    ";
        final StringBuilder logBuilder = new StringBuilder("Request [").append(Strings.LINE_SEPARATOR);

        logBuilder.append(indents).append("method=").append(httpServletRequest.getMethod()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("URL=").append(httpServletRequest.getRequestURL()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("contentType=").append(httpServletRequest.getContentType()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("characterEncoding=").append(httpServletRequest.getCharacterEncoding()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("local=[").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append(indents).append("addr=").append(httpServletRequest.getLocalAddr()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append(indents).append("port=").append(httpServletRequest.getLocalPort()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append(indents).append("name=").append(httpServletRequest.getLocalName()).append("],").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("remote=[").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append(indents).append("addr=").append(getRemoteAddr(httpServletRequest)).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append(indents).append("port=").append(httpServletRequest.getRemotePort()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append(indents).append("host=").append(httpServletRequest.getRemoteHost()).append("],").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("headers=[");
        final Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        final StringBuilder headerLogBuilder = new StringBuilder();
        if (null != headerNames) {
            logBuilder.append(Strings.LINE_SEPARATOR);
            while (headerNames.hasMoreElements()) {
                final String name = headerNames.nextElement();
                final String value = httpServletRequest.getHeader(name);
                headerLogBuilder.append(indents).append(indents).append(name).append("=").append(value);
                headerLogBuilder.append(Strings.LINE_SEPARATOR);
            }
            headerLogBuilder.append(indents);
        }
        headerLogBuilder.append("]");
        logBuilder.append(headerLogBuilder.toString());

        logBuilder.append(Strings.LINE_SEPARATOR).append("]");

        return logBuilder.toString();
    }

    /**
     * Gets the Internet Protocol (IP) address of the end-client that sent the specified request.
     * <p>
     * It will try to get HTTP head "X-forwarded-for" or "X-Real-IP" from the last proxy to get the request first, if not found, try to get
     * it directly by {@link HttpServletRequest#getRemoteAddr()}.
     * </p>
     *
     * @param request the specified request
     * @return the IP address of the end-client sent the specified request
     */
    public static String getRemoteAddr(final HttpServletRequest request) {
        String ret = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ret)) {
            ret = request.getHeader("X-Real-IP");
            if (StringUtils.isBlank(ret)) {
                return request.getRemoteAddr();
            }
        }

        return ret.split(",")[0];
    }

    /**
     * Gets the scheme of the end-client that sent the specified request.
     *
     * @param request the specified reuqest
     * @return scheme
     */
    public static String getServerScheme(final HttpServletRequest request) {
        String ret = request.getHeader("X-Forwarded-Scheme");
        if (StringUtils.isBlank(ret)) {
            ret = request.getHeader("X-Forwarded-Proto");
            if (StringUtils.isBlank(ret)) {
                return request.getScheme();
            }
        }

        return ret;
    }

    /**
     * Gets the server name of the end-client that sent the specified request.
     *
     * @param request the specified reuqest
     * @return server name
     */
    public static String getServerName(final HttpServletRequest request) {
        String ret = request.getHeader("X-Forwarded-Host");
        if (StringUtils.isBlank(ret)) {
            return request.getServerName();
        }

        return ret;
    }


    /**
     * Determines whether the specified request has been served.
     * <p>
     * A "served request" is a request a URI as former one. For example, if a client is request "/test", all requests from the client
     * subsequent in 24 hours will be treated as served requests, requested URIs save in client cookie (name: "visited").
     * </p>
     * <p>
     * If the specified request has not been served, appends the request URI in client cookie.
     * </p>
     * <p>
     * Sees this issue (https://github.com/b3log/solo/issues/44) for more details.
     * </p>
     *
     * @param request  the specified request
     * @param response the specified response
     * @return {@code true} if the specified request has been served, returns {@code false} otherwise
     */
    public static boolean hasBeenServed(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        if (null == cookies || 0 == cookies.length) {
            return false;
        }

        Cookie cookie;
        boolean needToCreate = true;
        boolean needToAppend = true;
        JSONArray cookieJSONArray = null;

        try {
            for (int i = 0; i < cookies.length; i++) {
                cookie = cookies[i];

                if (!"visited".equals(cookie.getName())) {
                    continue;
                }

                final String value = URLDecoder.decode(cookie.getValue(), "UTF-8");
                cookieJSONArray = new JSONArray(value);
                if (null == cookieJSONArray || 0 == cookieJSONArray.length()) {
                    return false;
                }

                needToCreate = false;

                for (int j = 0; j < cookieJSONArray.length(); j++) {
                    final String visitedURL = cookieJSONArray.optString(j);

                    if (request.getRequestURI().equals(visitedURL)) {
                        needToAppend = false;
                        return true;
                    }
                }
            }

            if (needToCreate) {
                final StringBuilder builder = new StringBuilder("[").append("\"").append(request.getRequestURI()).append("\"]");
                final Cookie c = new Cookie("visited", URLEncoder.encode(builder.toString(), "UTF-8"));
                c.setMaxAge(COOKIE_EXPIRY);
                c.setPath("/");
                response.addCookie(c);
            } else if (needToAppend) {
                cookieJSONArray.put(request.getRequestURI());

                final Cookie c = new Cookie("visited", URLEncoder.encode(cookieJSONArray.toString(), "UTF-8"));
                c.setMaxAge(COOKIE_EXPIRY);
                c.setPath("/");
                response.addCookie(c);
            }
        } catch (final Exception e) {
            final Cookie c = new Cookie("visited", null);
            c.setMaxAge(0);
            c.setPath("/");

            response.addCookie(c);
        }

        return false;
    }

    /**
     * Private constructor.
     */
    private Requests() {
    }
}
