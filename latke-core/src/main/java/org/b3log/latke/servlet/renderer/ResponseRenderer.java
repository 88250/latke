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
package org.b3log.latke.servlet.renderer;

import org.b3log.latke.servlet.RequestContext;

/**
 * The interface of all the renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Dec 4, 2018
 * @since 2.4.34
 */
public interface ResponseRenderer {

    /**
     * Pre-render before the real method be invoked.
     *
     * @param context the specified HTTP request context
     */
    void preRender(final RequestContext context);

    /**
     * Renders with the specified HTTP request context.
     *
     * @param context the specified HTTP request context
     */
    void render(final RequestContext context);

    /**
     * Post-render after the real method be invoked.
     *
     * @param context the specified HTTP request context
     */
    void postRender(final RequestContext context);
}
