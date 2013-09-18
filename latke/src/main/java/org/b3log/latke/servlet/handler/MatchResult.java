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


import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * User: mainlove
 * Date: 13-9-15
 * Time: 下午7:19
 */
public class MatchResult {

    private ProcessorInfo processorInfo;

    private String requestURI;

    private String matchedMethod;

    private String matchedPattern;



    private Map<String, Object> mapValues;

    private final List<AbstractHTTPResponseRenderer> rendererList = new ArrayList<AbstractHTTPResponseRenderer>();

    MatchResult(ProcessorInfo processorInfo, String requestURI, String matchedMethod, String matchedPattern) {
        this.processorInfo = processorInfo;
        this.requestURI = requestURI;
        this.matchedMethod = matchedMethod;
        this.matchedPattern = matchedPattern;
    }

    public MatchResult() {}

    public ProcessorInfo getProcessorInfo() {
        return processorInfo;
    }

    public void setProcessorInfo(ProcessorInfo processorInfo) {
        this.processorInfo = processorInfo;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getMatchedMethod() {
        return matchedMethod;
    }

    public void setMatchedMethod(String matchedMethod) {
        this.matchedMethod = matchedMethod;
    }

    public String getMatchedPattern() {
        return matchedPattern;
    }

    public void setMatchedPattern(String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }

    public Map<String, Object> getMapValues() {
        return mapValues;
    }

    public void setMapValues(Map<String, Object> mapValues) {
        this.mapValues = mapValues;
    }



    public void addRenders(AbstractHTTPResponseRenderer ins) {
        rendererList.add(ins);
    }

    public List<AbstractHTTPResponseRenderer> getRendererList() {
        return rendererList;
    }
}
