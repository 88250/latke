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
package org.b3log.latke.servlet.renderer;


import org.b3log.latke.servlet.HTTPRequestContext;

import java.util.Map;


/**
 * The interface of all the renderer.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Jan 22, 2013
 */
public interface HTTPResponseRenderer {

    /**
     * Pre-render before the real method be invoked.
     * 
     * @param context the specified HTTP request context
     * @param args the arguments of the real method
     */
    void preRender(final HTTPRequestContext context, final Map<String, Object> args);

    /**
     * Renders with the specified HTTP request context.
     * 
     * @param context the specified HTTP request context
     */
    void render(final HTTPRequestContext context);

    /**
     * Post-render after the real method be invoked.
     * 
     * @param context the specified HTTP request context
     * @param ret the return value of the real method
     */
    void postRender(final HTTPRequestContext context, final Object ret);
}
