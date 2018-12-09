/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Route handler
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
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
     * All context handler metas holder for routing.
     */
    private static final List<ContextHandlerMeta> CONTEXT_HANDLER_METAS = new ArrayList<>();

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
        final String requestURI = getRequestURI(request);
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
        final String contextPath = Latkes.getContextPath();

        // 精确匹配
        for (final ContextHandlerMeta contextHandlerMeta : CONTEXT_HANDLER_METAS) {
            for (final HttpMethod httpRequestMethod : contextHandlerMeta.getHttpMethod()) {
                if (httpMethod.equals(httpRequestMethod.toString())) {
                    final String[] uriPatterns = contextHandlerMeta.getPattern();
                    for (final String uriPattern : uriPatterns) {
                        if (uriPattern.equals(requestURI)) {
                            return new MatchResult(contextHandlerMeta, requestURI, httpMethod, uriPattern);
                        }
                    }
                }
            }
        }

        // 路径变量匹配
        for (final ContextHandlerMeta contextHandlerMeta : CONTEXT_HANDLER_METAS) {
            for (final HttpMethod httpRequestMethod : contextHandlerMeta.getHttpMethod()) {
                if (httpMethod.equals(httpRequestMethod.toString())) {
                    final String[] uriPatterns = contextHandlerMeta.getPattern();
                    for (final String uriPattern : uriPatterns) {
                        ret = route(contextPath + uriPattern, requestURI, httpMethod, contextHandlerMeta);
                        if (null != ret) {
                            return ret;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Routes the request specified by the given URI pattern, context handler meta, request URI and HTTP method.
     *
     * @param uriPattern         the given URI pattern
     * @param requestURI         the given request URI
     * @param method             the given HTTP method
     * @param contextHandlerMeta the given context handler meta
     * @return MatchResult, returns {@code null} if not found
     */
    private static MatchResult route(final String uriPattern, final String requestURI, final String method, final ContextHandlerMeta contextHandlerMeta) {
        final Map<String, String> resolveResult = UriTemplates.resolve(requestURI, uriPattern);
        if (null == resolveResult) {
            return null;
        }

        final MatchResult ret = new MatchResult(contextHandlerMeta, requestURI, method, uriPattern);
        ret.setPathVars(resolveResult);

        return ret;
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
                contextHandlerMeta.setPattern(requestProcessingMethodAnn.value());
                contextHandlerMeta.setHttpMethod(requestProcessingMethodAnn.method());
                contextHandlerMeta.setInvokeHolder(method);
                contextHandlerMeta.initProcessAdvices();

                addContextHandlerMeta(contextHandlerMeta);
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

        CONTEXT_HANDLER_METAS.add(contextHandlerMeta);
        LOGGER.log(Level.DEBUG, "Added a processor method [" + methodName + "]");
    }
}
