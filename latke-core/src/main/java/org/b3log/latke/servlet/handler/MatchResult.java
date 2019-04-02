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

import org.b3log.latke.servlet.renderer.AbstractResponseRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The route matched-result bean.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Dec 4, 2018
 */
public final class MatchResult {

    /**
     * Context handler meta..
     */
    private ContextHandlerMeta contextHandlerMeta;

    /**
     * Request URI.
     */
    private String requestURI;

    /**
     * Matched HTTP method.
     */
    private String matchedMethod;

    /**
     * Matched URI template.
     */
    private String matchedUriTemplate;

    /**
     * URI template name-args mappings.
     */
    private Map<String, String> pathVars = new HashMap<>();

    /**
     * the {@link AbstractResponseRenderer}  holders which be inited by sys, for advice to do the pre and post work.
     */
    private final List<AbstractResponseRenderer> rendererList = new ArrayList<>();

    /**
     * @param contextHandlerMeta context handler meta
     * @param requestURI         requestURI from request
     * @param matchedMethod      matched HTTP method
     * @param matchedUriTemplate matched URI template
     */
    MatchResult(final ContextHandlerMeta contextHandlerMeta, final String requestURI, final String matchedMethod, final String matchedUriTemplate) {
        this.contextHandlerMeta = contextHandlerMeta;
        this.requestURI = requestURI;
        this.matchedMethod = matchedMethod;
        this.matchedUriTemplate = matchedUriTemplate;
    }

    /**
     * Gets the matched URI template.
     *
     * @return matched URI template
     */
    public String getMatchedUriTemplate() {
        return matchedUriTemplate;
    }

    /**
     * Gets the context handler meta.
     *
     * @return context handler meta
     */
    public ContextHandlerMeta getContextHandlerMeta() {
        return contextHandlerMeta;
    }

    /**
     * addRenders.
     *
     * @param ins AbstractResponseRenderer
     */
    public void addRenders(final AbstractResponseRenderer ins) {
        rendererList.add(ins);
    }

    /**
     * Get path vars.
     *
     * @return path vars
     */
    public Map<String, String> getPathVars() {
        return pathVars;
    }

    /**
     * Set path vars.
     *
     * @param pathVars the specified path vars
     */
    public void setPathVars(final Map<String, String> pathVars) {
        this.pathVars = pathVars;
    }

    /**
     * getRendererList.
     *
     * @return rendererList
     */
    public List<AbstractResponseRenderer> getRendererList() {
        return rendererList;
    }
}
