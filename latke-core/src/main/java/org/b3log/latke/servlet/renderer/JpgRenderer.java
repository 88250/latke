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
import java.io.OutputStream;

/**
 * JPEG HTTP response renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Aug 2, 2018
 */
public final class JpgRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JpgRenderer.class);

    /**
     * Image data to render.
     */
    private byte[] data;

    /**
     * Sets the image with the specified image.
     *
     * @param data the specified image data
     */
    public void setImage(final byte[] data) {
        this.data = data;
    }

    @Override
    public void render(final RequestContext context) {
        try {
            final HttpServletResponse response = context.getResponse();
            response.setContentType("image/jpeg");

            final OutputStream outputStream = response.getOutputStream();
            outputStream.write(data);
            outputStream.close();
        } catch (final IOException e) {
            LOGGER.log(Level.ERROR, "Render JPG failed", e);
        }
    }
}
