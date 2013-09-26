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


import javax.servlet.RequestDispatcher;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;


/**
 *  servlet forward renderer.
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.0, Sep 26, 2013
 */
public class StaticFileRenderer extends AbstractHTTPResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticFileRenderer.class.getName());
    
    /**
     * HTTP {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR status} renderer.
     * 
     * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
     * @version 1.0.0.0, Sep 26, 2013
     */
    private RequestDispatcher requestDispatcher;

    /**
     * requestDispatcher holder.
     * @param requestDispatcher requestDispatcher
     */
    public StaticFileRenderer(final RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void render(final HTTPRequestContext context) {

        try {
            requestDispatcher.forward(context.getRequest(), context.getResponse());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "servlet forward error", e);
            throw new RuntimeException(e);
        }

    }

}
