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


import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * the matched-result bean.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
public class MatchResult {

    /**
     * the processorInfo which be matched.
     */
    private ProcessorInfo processorInfo;

    /**
     *  requestURI.
     */
    private String requestURI;

    /**
     * matchedMethod: http-method.
     */
    private String matchedMethod;

    /**
     * the real matchedPattern in processors.
     */
    private String matchedPattern;

    /**
     * the mapValue from url mapping.
     */
    private Map<String, Object> mapValues;

    /**
     * the {@link AbstractHTTPResponseRenderer}  holders which be inited by sys, for advice to do the pre and post work.
     */
    private final List<AbstractHTTPResponseRenderer> rendererList = new ArrayList<AbstractHTTPResponseRenderer>();

    /**
     *
     * @param processorInfo processorInfo
     * @param requestURI requestURI from request
     * @param matchedMethod matched http-Method
     * @param matchedPattern matchedPattern in processors
     */
    MatchResult(final ProcessorInfo processorInfo, final String requestURI, final String matchedMethod, final String matchedPattern) {
        this.processorInfo = processorInfo;
        this.requestURI = requestURI;
        this.matchedMethod = matchedMethod;
        this.matchedPattern = matchedPattern;
    }

    /**
     * the default constructor.
     */
    public MatchResult() {}

    /**
     * getProcessorInfo.
     * @return processorInfo
     */
    public ProcessorInfo getProcessorInfo() {
        return processorInfo;
    }

    /**
     *setProcessorInfo.
     * @param processorInfo processorInfo
     */
    public void setProcessorInfo(final ProcessorInfo processorInfo) {
        this.processorInfo = processorInfo;
    }

    /**
     * getRequestURI.
     * @return requestURI
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * setRequestURI.
     * @param requestURI requestURI
     */
    public void setRequestURI(final String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * getMatchedMethod.
     * @return  matchedMethod
     */
    public String getMatchedMethod() {
        return matchedMethod;
    }

    /**
     * setMatchedMethod.
     * @param matchedMethod matchedMethod
     */
    public void setMatchedMethod(final String matchedMethod) {
        this.matchedMethod = matchedMethod;
    }

    /**
     *   getMatchedPattern.
     * @return  matchedPattern
     */
    public String getMatchedPattern() {
        return matchedPattern;
    }

    /**
     * setMatchedPattern.
     * @param matchedPattern matchedPattern
     */
    public void setMatchedPattern(final String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }

    /**
     * getMapValues.
     * @return mapValues
     */
    public Map<String, Object> getMapValues() {
        return mapValues;
    }

    /**
     * setMapValues.
     * @param mapValues mapValues
     */
    public void setMapValues(final Map<String, Object> mapValues) {
        this.mapValues = mapValues;
    }

    /**
     * addRenders.
     * @param ins  AbstractHTTPResponseRenderer
     */
    public void addRenders(final AbstractHTTPResponseRenderer ins) {
        rendererList.add(ins);
    }

    /**
     * getRendererList.
     * @return rendererList
     */
    public List<AbstractHTTPResponseRenderer> getRendererList() {
        return rendererList;
    }
}
