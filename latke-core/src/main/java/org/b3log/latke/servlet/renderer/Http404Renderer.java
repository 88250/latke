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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HTTP {@link HttpServletResponse#SC_NOT_FOUND 404 status} renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Oct 31, 2011
 */
public final class Http404Renderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Http404Renderer.class);

    @Override
    public void render(final RequestContext context) {
        final HttpServletResponse response = context.getResponse();

        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Renders 404 error", e);
        }
    }
}
