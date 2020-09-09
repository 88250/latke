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
package org.b3log.latke.http.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.HttpMethod;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.util.UriTemplates;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Route handler
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.5, May 7, 2020
 * @since 2.4.34
 */
public class RouteHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(RouteHandler.class);

    // 以下 Map 按照分隔符 / 数量和 HTTP 请求方法分开，主要是为了让每种 Map 都比较小，这样查找更快。 https://ld246.com/article/1569318933910

    /**
     * One segment concrete URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> ONE_SEG_CONCRETE_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * One segment GET path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> ONE_SEG_GET_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * One segment POST path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> ONE_SEG_POST_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * One segment PUT path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> ONE_SEG_PUT_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * One segment DELETE path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> ONE_SEG_DELETE_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * One segment other HTTP methods path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> ONE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Two segments concrete URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> TWO_SEG_CONCRETE_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Two segments GET path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> TWO_SEG_GET_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Two segments POST path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> TWO_SEG_POST_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Two segments PUT path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> TWO_SEG_PUT_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Two segments DELETE path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> TWO_SEG_DELETE_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Two segments other HTTP methods path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> TWO_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Three segments concrete URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> THREE_SEG_CONCRETE_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Three segments GET path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> THREE_SEG_GET_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Three segments POST path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> THREE_SEG_POST_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Three segments PUT path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> THREE_SEG_PUT_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Three segments DELETE path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> THREE_SEG_DELETE_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Three segments other HTTP methods path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> THREE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Four and more segments concrete URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> FOUR_MORE_SEG_CONCRETE_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Four more segments GET path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> FOUR_MORE_SEG_GET_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Four more segments POST path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> FOUR_MORE_SEG_POST_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Four more segments PUT path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> FOUR_MORE_SEG_PUT_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Four more segments DELETE path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> FOUR_MORE_SEG_DELETE_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Four more segments other HTTP methods path var URI context handler metas holder.
     */
    private static final Map<String, ContextHandlerMeta> FOUR_MORE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS = new ConcurrentHashMap<>();

    /**
     * Public constructor.
     */
    public RouteHandler() {
    }

    @Override
    public void handle(final RequestContext context) {
        final Request request = context.getRequest();

        final long startTimeMillis = System.currentTimeMillis();
        request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);
        String requestURI = getRequestURI(request);
        requestURI = StringUtils.substringAfter(requestURI, Latkes.getContextPath()); // remove context path
        final String httpMethod = getHttpMethod(request);
        LOGGER.log(Level.DEBUG, "Request [requestURI={}, method={}]", requestURI, httpMethod);

        final RouteResolution result = doMatch(requestURI, httpMethod);
        if (null == result) {
            context.abort();
            return;
        }

        // 插入中间件
        final ContextHandlerMeta contextHandlerMeta = result.getContextHandlerMeta();
        final List<Handler> middlewares = contextHandlerMeta.getMiddlewares();
        for (int i = middlewares.size() - 1; 0 <= i; i--) {
            final Handler middleware = middlewares.get(i);
            context.insertHandlerAfter(middleware);
        }

        context.pathVars(result.getPathVars());
        context.attr(RequestContext.MATCH_RESULT, result);
        context.handle();
    }

    /**
     * Routes the request specified by the given request URI and HTTP method.
     *
     * @param requestURI the given request URI
     * @param httpMethod the given HTTP method
     * @return MatchResult, returns {@code null} if not found
     */
    public static RouteResolution doMatch(final String requestURI, final String httpMethod) {
        final int segs = StringUtils.countMatches(requestURI, "/");
        ContextHandlerMeta contextHandlerMeta;
        String concreteKey = httpMethod + "." + requestURI;
        switch (segs) {
            case 1:
                contextHandlerMeta = ONE_SEG_CONCRETE_CTX_HANDLER_METAS.get(concreteKey);
                if (null != contextHandlerMeta) {
                    return new RouteResolution(contextHandlerMeta, requestURI, httpMethod);
                }

                switch (httpMethod) {
                    case "GET":
                        return route(requestURI, httpMethod, ONE_SEG_GET_VAR_CTX_HANDLER_METAS);
                    case "POST":
                        return route(requestURI, httpMethod, ONE_SEG_POST_VAR_CTX_HANDLER_METAS);
                    case "PUT":
                        return route(requestURI, httpMethod, ONE_SEG_PUT_VAR_CTX_HANDLER_METAS);
                    case "DELETE":
                        return route(requestURI, httpMethod, ONE_SEG_DELETE_VAR_CTX_HANDLER_METAS);
                    default:
                        return route(requestURI, httpMethod, ONE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS);
                }
            case 2:
                contextHandlerMeta = TWO_SEG_CONCRETE_CTX_HANDLER_METAS.get(concreteKey);
                if (null != contextHandlerMeta) {
                    return new RouteResolution(contextHandlerMeta, requestURI, httpMethod);
                }

                switch (httpMethod) {
                    case "GET":
                        return route(requestURI, httpMethod, TWO_SEG_GET_VAR_CTX_HANDLER_METAS);
                    case "POST":
                        return route(requestURI, httpMethod, TWO_SEG_POST_VAR_CTX_HANDLER_METAS);
                    case "PUT":
                        return route(requestURI, httpMethod, TWO_SEG_PUT_VAR_CTX_HANDLER_METAS);
                    case "DELETE":
                        return route(requestURI, httpMethod, TWO_SEG_DELETE_VAR_CTX_HANDLER_METAS);
                    default:
                        return route(requestURI, httpMethod, TWO_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS);
                }
            case 3:
                contextHandlerMeta = THREE_SEG_CONCRETE_CTX_HANDLER_METAS.get(concreteKey);
                if (null != contextHandlerMeta) {
                    return new RouteResolution(contextHandlerMeta, requestURI, httpMethod);
                }

                switch (httpMethod) {
                    case "GET":
                        return route(requestURI, httpMethod, THREE_SEG_GET_VAR_CTX_HANDLER_METAS);
                    case "POST":
                        return route(requestURI, httpMethod, THREE_SEG_POST_VAR_CTX_HANDLER_METAS);
                    case "PUT":
                        return route(requestURI, httpMethod, THREE_SEG_PUT_VAR_CTX_HANDLER_METAS);
                    case "DELETE":
                        return route(requestURI, httpMethod, THREE_SEG_DELETE_VAR_CTX_HANDLER_METAS);
                    default:
                        return route(requestURI, httpMethod, THREE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS);
                }
            default:
                contextHandlerMeta = FOUR_MORE_SEG_CONCRETE_CTX_HANDLER_METAS.get(concreteKey);
                if (null != contextHandlerMeta) {
                    return new RouteResolution(contextHandlerMeta, requestURI, httpMethod);
                }

                switch (httpMethod) {
                    case "GET":
                        return route(requestURI, httpMethod, FOUR_MORE_SEG_GET_VAR_CTX_HANDLER_METAS);
                    case "POST":
                        return route(requestURI, httpMethod, FOUR_MORE_SEG_POST_VAR_CTX_HANDLER_METAS);
                    case "PUT":
                        return route(requestURI, httpMethod, FOUR_MORE_SEG_PUT_VAR_CTX_HANDLER_METAS);
                    case "DELETE":
                        return route(requestURI, httpMethod, FOUR_MORE_SEG_DELETE_VAR_CTX_HANDLER_METAS);
                    default:
                        return route(requestURI, httpMethod, FOUR_MORE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS);
                }
        }
    }


    /**
     * Adds the specified context handler meta
     *
     * @param contextHandlerMeta the specified context handler meta
     */
    public static void addContextHandlerMeta(final ContextHandlerMeta contextHandlerMeta) {
        final Method invokeHolder = contextHandlerMeta.getInvokeHolder();
        final Class<?> returnType = invokeHolder.getReturnType();
        final String methodName = invokeHolder.getDeclaringClass().getName() + "#" + invokeHolder.getName();

        if (!void.class.equals(returnType)) {
            LOGGER.error("Handler method [" + methodName + "] must return void");
            System.exit(-1);
        }
        final Class<?>[] exceptionTypes = invokeHolder.getExceptionTypes();
        if (0 < exceptionTypes.length) {
            LOGGER.error("Handler method [" + methodName + "] can not throw exceptions");
            System.exit(-1);
        }
        final Class<?>[] parameterTypes = invokeHolder.getParameterTypes();
        if (1 != parameterTypes.length) {
            LOGGER.error("Handler method [" + methodName + "] must have one parameter with type [RequestContext]");
            System.exit(-1);
        }
        final Class<?> parameterType = parameterTypes[0];
        if (!RequestContext.class.equals(parameterType)) {
            LOGGER.error("Handler method [" + methodName + "] must have one parameter with type [RequestContext]");
            System.exit(-1);
        }

        final HttpMethod[] httpMethods = contextHandlerMeta.getHttpMethods();
        for (int i = 0; i < httpMethods.length; i++) {
            final String httpMethod = httpMethods[i].name();
            final String[] uriTemplates = contextHandlerMeta.getUriTemplates();
            for (int j = 0; j < uriTemplates.length; j++) {
                final String uriTemplate = uriTemplates[j];
                final String key = httpMethod + "." + uriTemplate;
                final int segs = StringUtils.countMatches(uriTemplate, "/");
                if (!StringUtils.contains(uriTemplate, "{")) {
                    switch (segs) {
                        case 1:
                            ONE_SEG_CONCRETE_CTX_HANDLER_METAS.put(key, contextHandlerMeta);
                            break;
                        case 2:
                            TWO_SEG_CONCRETE_CTX_HANDLER_METAS.put(key, contextHandlerMeta);
                            break;
                        case 3:
                            THREE_SEG_CONCRETE_CTX_HANDLER_METAS.put(key, contextHandlerMeta);
                            break;
                        default:
                            FOUR_MORE_SEG_CONCRETE_CTX_HANDLER_METAS.put(key, contextHandlerMeta);
                    }
                } else { // URI templates contain path vars
                    switch (segs) {
                        case 1:
                            switch (httpMethod) {
                                case "GET":
                                    ONE_SEG_GET_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "POST":
                                    ONE_SEG_POST_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "PUT":
                                    ONE_SEG_PUT_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "DELETE":
                                    ONE_SEG_DELETE_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                default:
                                    ONE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                            }
                            break;
                        case 2:
                            switch (httpMethod) {
                                case "GET":
                                    TWO_SEG_GET_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "POST":
                                    TWO_SEG_POST_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "PUT":
                                    TWO_SEG_PUT_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "DELETE":
                                    TWO_SEG_DELETE_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                default:
                                    TWO_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                            }
                            break;
                        case 3:
                            switch (httpMethod) {
                                case "GET":
                                    THREE_SEG_GET_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "POST":
                                    THREE_SEG_POST_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "PUT":
                                    THREE_SEG_PUT_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "DELETE":
                                    THREE_SEG_DELETE_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                default:
                                    THREE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                            }
                            break;
                        default:
                            switch (httpMethod) {
                                case "GET":
                                    FOUR_MORE_SEG_GET_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "POST":
                                    FOUR_MORE_SEG_POST_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "PUT":
                                    FOUR_MORE_SEG_PUT_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                case "DELETE":
                                    FOUR_MORE_SEG_DELETE_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                                    break;
                                default:
                                    FOUR_MORE_SEG_OTHER_METHOD_VAR_CTX_HANDLER_METAS.put(uriTemplate, contextHandlerMeta);
                            }
                    }
                }
            }
        }

        LOGGER.log(Level.DEBUG, "Added a processor method [" + methodName + "]");
    }

    /**
     * Routes the specified request containing path vars with the specified path var context handler metas holder.
     *
     * @param requestURI                       the specified request URI
     * @param method                           the specified request method
     * @param pathVarContextHandlerMetasHolder the specified path var context handler metas holder
     * @return match result, returns {@code null} if not found
     */
    private static RouteResolution route(final String requestURI, final String method, final Map<String, ContextHandlerMeta> pathVarContextHandlerMetasHolder) {
        for (final Map.Entry<String, ContextHandlerMeta> entry : pathVarContextHandlerMetasHolder.entrySet()) {
            final String uriTemplate = entry.getKey();
            final ContextHandlerMeta contextHandlerMeta = entry.getValue();
            final Map<String, String> pathVars = UriTemplates.resolve(requestURI, uriTemplate);
            if (null == pathVars) {
                continue;
            }

            return new RouteResolution(contextHandlerMeta, pathVars, uriTemplate, method);
        }

        return null;
    }

    /**
     * Gets the HTTP method.
     *
     * @param request the specified request
     * @return HTTP method
     */
    private String getHttpMethod(final Request request) {
        String ret = (String) request.getAttribute(Keys.HttpRequest.REQUEST_METHOD);
        if (StringUtils.isBlank(ret)) {
            ret = request.getMethod();
        }

        return ret;
    }

    /**
     * Gets the request URI.
     *
     * @param request the specified request
     * @return requestURI
     */
    private String getRequestURI(final Request request) {
        String ret = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);
        if (StringUtils.isBlank(ret)) {
            ret = request.getRequestURI();
        }

        return ret;
    }
}
