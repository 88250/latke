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
import java.io.PrintWriter;

/**
 * XML HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Sep 22, 2011
 */
public final class TextXmlRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TextXmlRenderer.class);

    /**
     * Content to render.
     */
    private String content;

    /**
     * Sets the content with the specified content.
     *
     * @param content the specified content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public void render(final RequestContext context) {
        try {
            final HttpServletResponse response = context.getResponse();
            response.setContentType("text/xml");
            response.setCharacterEncoding("UTF-8");

            final PrintWriter writer = response.getWriter();
            writer.write(content);
            writer.close();
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Render failed", e);
        }
    }
}
