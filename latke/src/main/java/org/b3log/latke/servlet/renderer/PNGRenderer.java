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
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.image.Image;
import org.b3log.latke.servlet.HTTPRequestContext;

/**
 * JPEG HTTP response renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 12, 2011
 */
public final class PNGRenderer extends AbstractHTTPResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PNGRenderer.class.getName());
    /**
     * Image to render.
     */
    private Image image;

    /**
     * Sets the image with the specified image.
     * 
     * @param image the specified image
     */
    public void setImage(final Image image) {
        this.image = image;
    }

    @Override
    public void render(final HTTPRequestContext context) {
        try {
            final HttpServletResponse response = context.getResponse();
            response.setContentType("image/jpeg");

            final OutputStream outputStream = response.getOutputStream();
            outputStream.write(image.getData());
            outputStream.close();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Render failed", e);
        }
    }
}
