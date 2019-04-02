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

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Bean;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.UriTemplates;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Route handler
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 4, 2018
 * @since 2.4.34
 */
public class RouteHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RouteHandler.class);

    /**
     * The shared-matched-result-data name.
     */
    public static final String MATCH_RESULT = "MATCH_RESULT";

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
     * Public constructor..
     */
    public RouteHandler() {
        final BeanManager beanManager = BeanManager.getInstance();
        final Set<Bean<?>> processBeans = beanManager.getBeans(RequestProcessor.class);
        generateContextHandlerMeta(processBeans);
    }

    @Override
    public void handle(final RequestContext context) {
        final HttpServletRequest request = context.getRequest();

        final long startTimeMillis = System.currentTimeMillis();
        request.setAttribute(Keys.HttpRequest.START_TIME_MILLIS, startTimeMillis);
        String requestURI = getRequestURI(request);
        requestURI = StringUtils.substringAfter(requestURI, Latkes.getContextPath()); // remove servlet container context path
        final String httpMethod = getHttpMethod(request);
        LOGGER.log(Level.DEBUG, "Request [requestURI={0}, method={1}]", requestURI, httpMethod);

        final MatchResult result = doMatch(requestURI, httpMethod);
        if (result != null) {
            context.pathVars(result.getPathVars());
            context.attr(MATCH_RESULT, result);
            context.handle();
        }

        context.abort();
    }

    /**
     * Routes the request specified by the given request URI and HTTP method.
     *
     * @param requestURI the given request URI
     * @param httpMethod the given HTTP method
     * @return MatchResult, returns {@code null} if not found
     */
    public static MatchResult doMatch(final String requestURI, final String httpMethod) {
        MatchResult ret;
        final int segs = StringUtils.countMatches(requestURI, "/");
        ContextHandlerMeta contextHandlerMeta;
        String concreteKey = httpMethod + "." + requestURI;
        switch (segs) {
            case 1:
                contextHandlerMeta = ONE_SEG_CONCRETE_CTX_HANDLER_METAS.get(concreteKey);
                if (null != contextHandlerMeta) {
                    return new MatchResult(contextHandlerMeta, requestURI, httpMethod, requestURI);
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
                    return new MatchResult(contextHandlerMeta, requestURI, httpMethod, requestURI);
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
                    return new MatchResult(contextHandlerMeta, requestURI, httpMethod, requestURI);
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
                    return new MatchResult(contextHandlerMeta, requestURI, httpMethod, requestURI);
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
     * Routes the specified request URI containing path vars with the specified HTTP method and path var context handler metas holder.
     *
     * @param requestURI                       the specified request URI
     * @param httpMethod                       the specified HTTP method
     * @param pathVarContextHandlerMetasHolder the specified path var context handler metas holder
     * @return match result, returns {@code null} if not found
     */
    private static MatchResult route(final String requestURI, final String httpMethod, final Map<String, ContextHandlerMeta> pathVarContextHandlerMetasHolder) {
        MatchResult ret;
        for (final Map.Entry<String, ContextHandlerMeta> entry : pathVarContextHandlerMetasHolder.entrySet()) {
            final String uriTemplate = entry.getKey();
            final ContextHandlerMeta contextHandlerMeta = entry.getValue();
            final Map<String, String> resolveResult = UriTemplates.resolve(requestURI, uriTemplate);
            if (null == resolveResult) {
                continue;
            }

            ret = new MatchResult(contextHandlerMeta, requestURI, httpMethod, uriTemplate);
            ret.setPathVars(resolveResult);

            return ret;
        }

        return null;
    }

    /**
     * Gets the HTTP method.
     *
     * @param request the specified request
     * @return HTTP method
     */
    private String getHttpMethod(final HttpServletRequest request) {
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
    private String getRequestURI(final HttpServletRequest request) {
        String ret = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);
        if (StringUtils.isBlank(ret)) {
            ret = request.getRequestURI();
        }

        return ret;
    }

    /**
     * Scan beans to get the context handler meta.
     *
     * @param processBeans processBeans which contains {@link RequestProcessor}
     */
    private void generateContextHandlerMeta(final Set<Bean<?>> processBeans) {
        for (final Bean<?> latkeBean : processBeans) {
            final Class<?> clz = latkeBean.getBeanClass();
            final Method[] declaredMethods = clz.getDeclaredMethods();
            for (int i = 0; i < declaredMethods.length; i++) {
                final Method method = declaredMethods[i];
                final RequestProcessing requestProcessingMethodAnn = method.getAnnotation(RequestProcessing.class);
                if (null == requestProcessingMethodAnn) {
                    continue;
                }

                final ContextHandlerMeta contextHandlerMeta = new ContextHandlerMeta();
                contextHandlerMeta.setUriTemplates(requestProcessingMethodAnn.value());
                contextHandlerMeta.setHttpMethods(requestProcessingMethodAnn.method());
                contextHandlerMeta.setInvokeHolder(method);
                contextHandlerMeta.initProcessAdvices();

                addContextHandlerMeta(contextHandlerMeta);
            }
        }
    }
}
