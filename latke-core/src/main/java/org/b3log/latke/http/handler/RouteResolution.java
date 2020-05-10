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

import java.util.HashMap;
import java.util.Map;

/**
 * The route resolution.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 10, 2020
 * @since 3.2.4
 */
public final class RouteResolution {

    /**
     * Context handler meta..
     */
    private final ContextHandlerMeta contextHandlerMeta;

    /**
     * URI template name-args mappings.
     */
    private Map<String, String> pathVars = new HashMap<>();

    /**
     * Matched URI template.
     */
    private final String matchedUriTemplate;

    /**
     * Matched HTTP method.
     */
    private final String matchedMethod;

    /**
     * Constructs a rote resolution with the specified context handler meta.
     *
     * @param contextHandlerMeta the specified context handler meta
     * @param matchedUriTemplate the specified matched URI template
     * @param matchedMethod      the specified matched method
     */
    public RouteResolution(final ContextHandlerMeta contextHandlerMeta, final String matchedUriTemplate, final String matchedMethod) {
        this.contextHandlerMeta = contextHandlerMeta;
        this.matchedUriTemplate = matchedUriTemplate;
        this.matchedMethod = matchedMethod;
    }

    /**
     * Constructs a rote resolution with the specified context handler meta and path vars.
     *
     * @param contextHandlerMeta the specified context handler meta
     * @param pathVars           the specified path vars
     * @param matchedUriTemplate the specified matched URI template
     * @param matchedMethod      the specified matched method
     */
    public RouteResolution(final ContextHandlerMeta contextHandlerMeta, final Map<String, String> pathVars, final String matchedUriTemplate, final String matchedMethod) {
        this.contextHandlerMeta = contextHandlerMeta;
        this.pathVars = pathVars;
        this.matchedUriTemplate = matchedUriTemplate;
        this.matchedMethod = matchedMethod;
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
     * Get path vars.
     *
     * @return path vars
     */
    public Map<String, String> getPathVars() {
        return pathVars;
    }

    /**
     * Gets matched URI template.
     *
     * @return matched URI template
     */
    public String getMatchedUriTemplate() {
        return matchedUriTemplate;
    }

    /**
     * Gets matched method.
     *
     * @return matched method
     */
    public String getMatchedMethod() {
        return matchedMethod;
    }
}
