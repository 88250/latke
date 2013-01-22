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
package org.b3log.latke.servlet.renderer;


import java.util.Map;

import org.b3log.latke.servlet.HTTPRequestContext;


/**
 * the interface of all the renderer.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Jan 22, 2013
 */
public interface HTTPResponseRenderer {

    /**
     * pre-render before the real method be invoked.
     * @param context context
     * @param args args
     */
    void preRender(final HTTPRequestContext context, final Map<String, Object> args);

    /**
     * Renders with the specified HTTP request context.
     * @param context the specified HTTP request context
     */
    void render(final HTTPRequestContext context);

    /**
     * post-render after the real method be invoked.
     * @param context context
     * @param ret ret
     */
    void postRender(final HTTPRequestContext context, final Object ret);
}
