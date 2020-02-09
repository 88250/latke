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
package org.b3log.latke.http.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * The route resolution.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Feb 9, 2020
 * @since 3.2.4
 */
public final class RouteResolution {

    /**
     * Context handler meta..
     */
    private ContextHandlerMeta contextHandlerMeta;

    /**
     * URI template name-args mappings.
     */
    private Map<String, String> pathVars = new HashMap<>();

    /**
     * Constructs a rote resolution with the specified context handler meta.
     *
     * @param contextHandlerMeta the specified context handler meta
     */
    public RouteResolution(final ContextHandlerMeta contextHandlerMeta) {
        this.contextHandlerMeta = contextHandlerMeta;
    }

    /**
     * Constructs a rote resolution with the specified context handler meta and path vars.
     *
     * @param contextHandlerMeta the specified context handler meta
     * @param pathVars           the specified path vars
     */
    public RouteResolution(final ContextHandlerMeta contextHandlerMeta, final Map<String, String> pathVars) {
        this.contextHandlerMeta = contextHandlerMeta;
        this.pathVars = pathVars;
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
}
