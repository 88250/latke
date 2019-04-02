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

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;

import javax.servlet.RequestDispatcher;

/**
 * servlet forward renderer.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.0, Sep 26, 2013
 */
public class StaticFileRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticFileRenderer.class);

    /**
     * Request dispatcher.
     */
    private RequestDispatcher requestDispatcher;

    /**
     * Request dispatcher.
     *
     * @param requestDispatcher request dispatcher
     */
    public StaticFileRenderer(final RequestDispatcher requestDispatcher) {
        this.requestDispatcher = requestDispatcher;
    }

    @Override
    public void render(final RequestContext context) {
        try {
            requestDispatcher.forward(context.getRequest(), context.getResponse());
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Default servlet forward error", e);
        }
    }
}
