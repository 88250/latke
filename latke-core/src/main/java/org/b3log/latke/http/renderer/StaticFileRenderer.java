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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.util.URLs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
                final File file = resolveStaticFile(new File(path), uri);
                if (null == file || !file.isFile()) {
                    response.sendError0(404);
                    return;
                }

                bytes = FileUtils.readFileToByteArray(file);
            } else {
                final String resourcePath = StringUtils.prependIfMissing(StringUtils.removeStart(uri, "/"), "/");
                if (StringUtils.contains(resourcePath, "..")) {
                    response.sendError0(404);
                    return;
                }
                try (final InputStream inputStream = StaticFileRenderer.class.getResourceAsStream(resourcePath)) {
                    if (null == inputStream) {
                        response.sendError0(404);
                        return;
                    }

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

    /**
     * Resolves a static file inside the specified base directory.
     * <p>
     * The URI has already been decoded by the caller. The resolved path must stay inside the base directory,
     * including after resolving symbolic links.
     * </p>
     *
     * @param baseDir the static resource base directory
     * @param uri     the decoded request URI
     * @return the resolved file, returns {@code null} if the URI escapes the base directory
     * @throws IOException io exception
     */
    static File resolveStaticFile(final File baseDir, final String uri) throws IOException {
        if (null == baseDir || StringUtils.isBlank(uri) || uri.indexOf('\0') >= 0) {
            return null;
        }

        final Path base = baseDir.toPath().toRealPath();
        final Path target = base.resolve(StringUtils.removeStart(uri, "/")).normalize();
        if (!target.startsWith(base)) {
            return null;
        }

        if (Files.exists(target)) {
            final Path real = target.toRealPath();
            if (!real.startsWith(base)) {
                return null;
            }

            return real.toFile();
        }

        return target.toFile();
    }
}
