/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;

import java.util.Iterator;

/**
 * Request utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="https://ld246.com/member/e">Dongxu Wang</a>
 * @version 3.1.0.1, Apr 2, 2021
 */
public final class Requests {

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
     * @param request the specified HTTP request
     * @param level   the specified logging level
     * @param logger  the specified logger
     */
    public static void log(final Request request, final Level level, final Logger logger) {
        if (!logger.isEnabled(level)) {
            return;
        }
        logger.log(level, getLog(request));
    }

    /**
     * Gets log of the specified request.
     *
     * @param request the specified request
     * @return log
     */
    public static String getLog(final Request request) {
        if (null == request) {
            return "request is null";
        }

        final String indents = "    ";
        final StringBuilder logBuilder = new StringBuilder("Request [").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("method=").append(request.getMethod()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("URI=").append(request.getRequestURI()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("contentType=").append(request.getContentType()).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("remoteAddr=").append(getRemoteAddr(request)).append(",").append(Strings.LINE_SEPARATOR);
        logBuilder.append(indents).append("headers=[");
        final Iterator<String> headerNames = request.getHeaderNames();
        final StringBuilder headerLogBuilder = new StringBuilder();
        logBuilder.append(Strings.LINE_SEPARATOR);
        while (headerNames.hasNext()) {
            final String name = headerNames.next();
            final String value = request.getHeader(name);
            headerLogBuilder.append(indents).append(indents).append(name).append("=").append(value);
            headerLogBuilder.append(Strings.LINE_SEPARATOR);
        }
        headerLogBuilder.append(indents);
        headerLogBuilder.append("]");
        logBuilder.append(headerLogBuilder.toString());
        logBuilder.append(Strings.LINE_SEPARATOR).append("]");
        return logBuilder.toString();
    }

    /**
     * Gets the Internet Protocol (IP) address of the end-client that sent the specified request.
     * <p>
     * It will try to get HTTP header "Ali-CDN-Real-IP", "CF-Connecting-IP", "X-forwarded-for" and "X-Real-IP" from the
     * last proxy to get the request first, if not found, try to get it directly by {@link Request#getRemoteAddr()}.
     * </p>
     *
     * @param request the specified request
     * @return the IP address of the end-client sent the specified request
     */
    public static String getRemoteAddr(final Request request) {
        String ret = request.getHeader("Ali-CDN-Real-IP");
        if (StringUtils.isNotBlank(ret)) {
            return ret;
        }

        ret = request.getHeader("CF-Connecting-IP");
        if (StringUtils.isNotBlank(ret)) {
            return ret;
        }

        ret = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ret)) {
            ret = request.getHeader("X-Real-IP");
            if (StringUtils.isBlank(ret)) {
                return request.getRemoteAddr();
            }
        }

        final String[] parts = StringUtils.split(ret, ",");
        ret = parts[parts.length - 1];
        ret = StringUtils.trim(ret);
        return ret;
    }

    /**
     * Gets the Internet Protocol (IP) address of the end-client that sent the specified request.
     *
     * @param context the specified request context
     * @return the IP address of the end-client sent the specified request
     * @see #getRemoteAddr(Request)
     */
    public static String getRemoteAddr(final RequestContext context) {
        return getRemoteAddr(context.getRequest());
    }

    /**
     * Gets the scheme of the end-client that sent the specified request.
     *
     * @param request the specified request
     * @return scheme
     */
    public static String getServerScheme(final Request request) {
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
     * @param request the specified request
     * @return server name
     */
    public static String getServerName(final Request request) {
        String ret = request.getHeader("X-Forwarded-Host");
        if (StringUtils.isBlank(ret)) {
            return request.getServerName();
        }
        return ret;
    }

    /**
     * Private constructor.
     */
    private Requests() {
    }
}
