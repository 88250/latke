/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.converter.ConvertSupport;
import org.b3log.latke.util.AntPathMatcher;
import org.b3log.latke.util.RegexMatcher;
import org.b3log.latke.util.RegexPathMatcher;
import org.b3log.latke.util.Strings;
import org.weborganic.furi.URIResolveResult;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;


/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午3:42
 */
public class RequestMatchHandler implements Ihandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestMatchHandler.class.getName());
    /**
     * the shared-matched-result-data name.
     */
    private static final String MATCH_RESULT = "MATCH_RESULT";

    /**
     * all processors holder for match.
     */
    private final List<ProcessorInfo> processorInfos = new ArrayList<ProcessorInfo>();

    public RequestMatchHandler() {

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final Set<LatkeBean<?>> processBeans = beanManager.getBeans(RequestProcessor.class);

        genInfo(processBeans);
    }

    @Override
    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception {
        HttpServletRequest request = context.getRequest();

        String requestURI = getRequestURI(request);
        String method = getMethod(request);

        LOGGER.log(Level.DEBUG, "Request[requestURI={0}, method={1}]", new Object[]{requestURI, method});

        MatchResult result = doMatch(requestURI, method);

        if (result != null) {
            //do loger
            httpControl.data(MATCH_RESULT, result);
            httpControl.nextHandler();
        } else {
            //TODO
        }
    }

    private MatchResult doMatch(String requestURI, String method) {

        MatchResult ret = null;

        for (ProcessorInfo processorInfo : processorInfos) {

            for (HTTPRequestMethod httpRequestMethod : processorInfo.getHttpMethod()) {
                if (method.equals(httpRequestMethod)) {

                    String[] uriPatterns = processorInfo.getPattern();

                    for (String uriPattern : uriPatterns) {
                        ret = getResult(uriPattern, processorInfo, requestURI, method);
                        if (ret != null) {
                            return ret;
                        }
                    }
                }
            }
        }
        return ret;

    }

    private MatchResult getResult(String uriPattern, ProcessorInfo processorInfo, String requestURI, String method) {

        if (requestURI.equals(uriPattern)) {
            return new MatchResult(processorInfo, requestURI, method, uriPattern);
        }

        switch (processorInfo.getUriPatternMode()) {

            case ANT_PATH:
                boolean ret = AntPathMatcher.match(uriPattern, requestURI);
                if (ret) {
                    return new MatchResult(processorInfo, requestURI, method, uriPattern);
                }
                break;

            case REGEX:
                URIResolveResult rett = RegexMatcher.match(uriPattern, requestURI);
                if (rett.getStatus().equals(URIResolveResult.Status.RESOLVED)) {
                    MatchResult result = new MatchResult(processorInfo, requestURI, method, uriPattern);

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    for (String s : rett.names()) {
                        map.put(s, rett.get(s));
                    }
                    result.setMapValues(map);
                    return result;
                }
                break;

            default:
                throw new IllegalStateException(
                        "Can not process URI pattern[uriPattern=" + uriPattern + ", mode="
                                + processorInfo.getUriPatternMode() + "]");
        }
        return null;
    }

    private String getMethod(HttpServletRequest request) {
        String method = (String) request.getAttribute(Keys.HttpRequest.REQUEST_METHOD);

        if (Strings.isEmptyOrNull(method)) {
            method = request.getMethod();
        }
        return method;
    }

    private String getRequestURI(HttpServletRequest request) {
        String requestURI = (String) request.getAttribute(Keys.HttpRequest.REQUEST_URI);

        if (Strings.isEmptyOrNull(requestURI)) {
            requestURI = request.getRequestURI();
        }
        return requestURI;
    }

    private void genInfo(Set<LatkeBean<?>> processBeans) {

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

    private void addProcessorInfo(RequestProcessing requestProcessingMethodAnn, Method mthd) {

        // anotation to bean

        ProcessorInfo processorInfo = new ProcessorInfo();
        processorInfo.setPattern(requestProcessingMethodAnn.value());
        processorInfo.setUriPatternMode(requestProcessingMethodAnn.uriPatternsMode());
        processorInfo.setHttpMethod(requestProcessingMethodAnn.method());
        processorInfo.setConvertClass(requestProcessingMethodAnn.convertClass());

        processorInfo.setInvokeHolder(mthd);

    }

}


class ProcessorInfo {

    private String[] pattern;
    private URIPatternMode uriPatternMode;
    private HTTPRequestMethod[] httpMethod;
    private Method invokeHolder;
    private Class<? extends ConvertSupport> convertClass;

    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setUriPatternMode(URIPatternMode uriPatternMode) {
        this.uriPatternMode = uriPatternMode;
    }

    public URIPatternMode getUriPatternMode() {
        return uriPatternMode;
    }

    public void setHttpMethod(HTTPRequestMethod[] httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HTTPRequestMethod[] getHttpMethod() {
        return httpMethod;
    }

    public void setInvokeHolder(Method invokeHolder) {
        this.invokeHolder = invokeHolder;
    }

    public Method getInvokeHolder() {
        return invokeHolder;
    }

    public void setConvertClass(Class<? extends ConvertSupport> convertClass) {
        this.convertClass = convertClass;
    }

    public Class<? extends ConvertSupport> getConvertClass() {
        return convertClass;
    }
}

class MatchResult {

    private ProcessorInfo processorInfo;

    private String requestURI;

    private String matchedMethod;

    private String matchedPattern;

    private Map<String, Object> mapValues;

    MatchResult(ProcessorInfo processorInfo, String requestURI, String matchedMethod, String matchedPattern) {
        this.processorInfo = processorInfo;
        this.requestURI = requestURI;
        this.matchedMethod = matchedMethod;
        this.matchedPattern = matchedPattern;
    }

    public MatchResult() {
    }

    ProcessorInfo getProcessorInfo() {
        return processorInfo;
    }

    void setProcessorInfo(ProcessorInfo processorInfo) {
        this.processorInfo = processorInfo;
    }

    Map<String, Object> getMapValues() {
        return mapValues;
    }

    void setMapValues(Map<String, Object> mapValues) {
        this.mapValues = mapValues;
    }

    String getRequestURI() {
        return requestURI;
    }

    void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    String getMatchedMethod() {
        return matchedMethod;
    }

    void setMatchedMethod(String matchedMethod) {
        this.matchedMethod = matchedMethod;
    }

    String getMatchedPattern() {
        return matchedPattern;
    }

    void setMatchedPattern(String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }
}
