/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.DefaultMatcher;
import org.b3log.latke.util.RegexPathMatcher;
import org.b3log.latke.util.Strings;
import org.weborganic.furi.URIResolveResult;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.b3log.latke.Latkes;
import org.b3log.latke.util.AntPathMatcher;

/**
 * to match one method of processor to do the reqest handler.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Dec 23, 2015
 */
public class RequestDispatchHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestDispatchHandler.class);

    /**
     * the shared-matched-result-data name.
     */
    public static final String MATCH_RESULT = "MATCH_RESULT";

    /**
     * all processors holder for match.
     */
    private final List<ProcessorInfo> processorInfos = new ArrayList<>();

    /**
     * constructor.
     */
    public RequestDispatchHandler() {

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final Set<LatkeBean<?>> processBeans = beanManager.getBeans(RequestProcessor.class);

        // do scan in  beanManager
        genInfo(processBeans);
    }

    @Override
    public void handle(final HTTPRequestContext context, final HttpControl httpControl) throws Exception {
        final HttpServletRequest request = context.getRequest();

        final String requestURI = getRequestURI(request);
        final String httpMethod = getHTTPMethod(request);

        LOGGER.log(Level.DEBUG, "Request[requestURI={0}, method={1}]", new Object[]{requestURI, httpMethod});

        final MatchResult result = doMatch(requestURI, httpMethod);

        if (result != null) {
            // do logger
            httpControl.data(MATCH_RESULT, result);
            httpControl.nextHandler();
        }
    }

    /**
     * doMatch.
     *
     * @param requestURI requestURI
     * @param httpMethod http-method
     * @return MatchResult
     */
    // XXX: Performance Issue 
    private MatchResult doMatch(final String requestURI, final String httpMethod) {
        MatchResult ret = null;

        final String contextPath = Latkes.getContextPath();

        for (ProcessorInfo processorInfo : processorInfos) {
            for (HTTPRequestMethod httpRequestMethod : processorInfo.getHttpMethod()) {
                if (httpMethod.equals(httpRequestMethod.toString())) {
                    final String[] uriPatterns = processorInfo.getPattern();

                    for (String uriPattern : uriPatterns) {
                        ret = getResult(contextPath + uriPattern, processorInfo, requestURI, httpMethod);

                        if (null != ret) {
                            return ret;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * get MatchResult.
     *
     * @param uriPattern uriPattern
     * @param processorInfo processorInfo
     * @param requestURI requestURI
     * @param method http method
     * @return MatchResult
     */
    private MatchResult getResult(final String uriPattern, final ProcessorInfo processorInfo, final String requestURI, final String method) {

        if (requestURI.equals(uriPattern)) {
            return new MatchResult(processorInfo, requestURI, method, uriPattern);
        }

        switch (processorInfo.getUriPatternMode()) {
            case REGEX:
                if (RegexPathMatcher.match(uriPattern, requestURI)) {
                    return new MatchResult(processorInfo, requestURI, method, uriPattern);
                }

                break;

            case ANT_PATH:
                if (AntPathMatcher.match(uriPattern, requestURI)) {
                    return new MatchResult(processorInfo, requestURI, method, uriPattern);
                }

                final URIResolveResult result = DefaultMatcher.match(uriPattern, requestURI);

                if (URIResolveResult.Status.RESOLVED == result.getStatus()) {
                    final MatchResult ret = new MatchResult(processorInfo, requestURI, method, uriPattern);

                    final HashMap<String, Object> map = new HashMap<String, Object>();

                    for (String s : result.names()) {
                        map.put(s, result.get(s));
                    }

                    ret.setMapValues(map);

                    return ret;
                }

                break;

            default:
                throw new IllegalStateException(
                        "Can not process URI pattern[uriPattern=" + uriPattern + ", mode=" + processorInfo.getUriPatternMode() + "]");
        }

        return null;
    }

    /**
     * get real-Http-method.
     *
     * @param request request
     * @return http-method
     */
    private String getHTTPMethod(final HttpServletRequest request) {
        String ret = (String) request.getAttribute(Keys.HttpRequest.REQUEST_METHOD);

        if (Strings.isEmptyOrNull(ret)) {
            ret = request.getMethod();
        }

        return ret;
    }

    /**
     * get real-requestURI.
     *
     * @param request request
     * @return requestURI
     */
    private String getRequestURI(final HttpServletRequest request) {
        String ret = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);

        if (Strings.isEmptyOrNull(ret)) {
            ret = request.getRequestURI();
        }

        return ret;
    }

    /**
     * scan beans to get the processor info.
     *
     * @param processBeans processBeans which contains {@link RequestProcessor}
     */
    private void genInfo(final Set<LatkeBean<?>> processBeans) {

        for (final LatkeBean<?> latkeBean : processBeans) {
            final Class<?> clz = latkeBean.getBeanClass();

            final Method[] declaredMethods = clz.getDeclaredMethods();

            for (int i = 0; i < declaredMethods.length; i++) {
                final Method mthd = declaredMethods[i];
                final RequestProcessing requestProcessingMethodAnn = mthd.getAnnotation(RequestProcessing.class);

                if (null == requestProcessingMethodAnn) {
                    continue;
                }

                LOGGER.log(Level.DEBUG, "Added a processor method[className={0}], method[{1}]",
                        new Object[]{clz.getCanonicalName(), mthd.getName()});

                addProcessorInfo(requestProcessingMethodAnn, mthd);
            }
        }
    }

    /**
     * addProcessorInfo.
     *
     * @param requestProcessingMethodAnn requestProcessingMethodAnn
     * @param mthd the invoke method
     */
    private void addProcessorInfo(final RequestProcessing requestProcessingMethodAnn, final Method mthd) {

        // anotation to bean
        final ProcessorInfo processorInfo = new ProcessorInfo();

        processorInfo.setPattern(requestProcessingMethodAnn.value());
        processorInfo.setUriPatternMode(requestProcessingMethodAnn.uriPatternsMode());
        processorInfo.setHttpMethod(requestProcessingMethodAnn.method());
        processorInfo.setConvertClass(requestProcessingMethodAnn.convertClass());
        processorInfo.setInvokeHolder(mthd);

        processorInfos.add(processorInfo);

    }
}
