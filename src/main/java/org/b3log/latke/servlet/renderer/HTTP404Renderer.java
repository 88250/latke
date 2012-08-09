/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.servlet.HTTPRequestContext;

/**
 * HTTP {@link HttpServletResponse#SC_NOT_FOUND 404 status} renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 31, 2011
 */
public final class HTTP404Renderer extends AbstractHTTPResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(HTTP404Renderer.class.getName());

    @Override
    public void render(final HTTPRequestContext context) {
        final HttpServletResponse response = context.getResponse();

        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Renders 404 error", e);
        }
    }
}
