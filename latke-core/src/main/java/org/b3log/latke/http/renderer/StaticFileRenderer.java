/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.http.renderer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.util.URLs;

import java.io.File;
import java.io.InputStream;

/**
 * Static file renderer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.3.0, Apr 17, 2020
 * @since 1.0.0
 */
public class StaticFileRenderer extends AbstractResponseRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(StaticFileRenderer.class);

    private static final Tika TIKA = new Tika();

    @Override
    public void render(final RequestContext context) {
        final Response response = context.getResponse();
        try {
            String uri = context.requestURI();
            uri = URLs.decode(uri);
            if (StringUtils.contains(uri, Latkes.getStaticPath())) {
                uri = StringUtils.substringAfter(uri, Latkes.getStaticPath());
            }

            byte[] bytes;
            if (!Latkes.isInJar()) {
                String path = Latkes.class.getResource("/latke.properties").getPath();
                path = StringUtils.substringBeforeLast(path, "latke.properties");
                path = URLs.decode(path);
                if (StringUtils.contains(path, "/target/classes/") || StringUtils.contains(path, "/target/test-classes/")) {
                    // 开发时使用源码目录
                    path = StringUtils.replace(path, "/target/classes/", "/src/main/resources/");
                    path = StringUtils.replace(path, "/target/test-classes/", "/src/main/resources/");
                }
                path += uri;
                final File file = new File(path);
                if (!file.exists()) {
                    response.sendError0(404);
                    return;
                }

                bytes = FileUtils.readFileToByteArray(file);
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
