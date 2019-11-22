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
package org.b3log.latke.http.renderer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.URLs;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Static file renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Nov 22, 2019
 */
public class StaticFileRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StaticFileRenderer.class);

    private static final Tika TIKA = new Tika();

    @Override
    public void render(final RequestContext context) {
        final Response response = context.getResponse();
        try {
            String uri = context.requestURI();
            uri = URLs.decode(uri);
            byte[] bytes;

            if (!Latkes.isInJar()) {
                String path = StaticFileRenderer.class.getResource("/").getPath();
                if (StringUtils.contains(path, "/target/classes/") || StringUtils.contains(path, "/target/test-classes/")) {
                    // 开发时使用源码目录
                    path = StringUtils.replace(path, "/target/classes/", "/src/main/resources/");
                    path = StringUtils.replace(path, "/target/test-classes/", "/src/main/resources/");
                }
                path += uri;
                bytes = FileUtils.readFileToByteArray(new File(path));
            } else {
                try (final InputStream inputStream = StaticFileRenderer.class.getResourceAsStream(uri)) {
                    bytes = IOUtils.toByteArray(inputStream);
                }
            }

            final String contentType = TIKA.detect(uri);
            response.setContentType(contentType);
            response.sendBytes(bytes);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Renders static file failed", e);
            response.sendError0(500);
        }
    }
}
