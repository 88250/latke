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

import org.testng.annotations.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Static file renderer test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Nov 11, 2026
 * @since 3.5.2
 */
public class StaticFileRendererTestCase {

    @Test
    public void resolvesFileInsideBaseDirectory() throws Exception {
        final Path base = Files.createTempDirectory("latke-static");
        final Path file = base.resolve("emoji/graphics/smile.png");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "png", StandardCharsets.UTF_8);

        final File resolved = StaticFileRenderer.resolveStaticFile(base.toFile(), "/emoji/graphics/smile.png");

        assertNotNull(resolved);
        assertEquals(resolved.getCanonicalPath(), file.toFile().getCanonicalPath());
    }

    @Test
    public void rejectsParentDirectoryTraversal() throws Exception {
        final Path base = Files.createTempDirectory("latke-static");
        final Path secret = base.getParent().resolve("latke-secret.txt");
        Files.writeString(secret, "secret", StandardCharsets.UTF_8);

        assertNull(StaticFileRenderer.resolveStaticFile(base.toFile(), "/../latke-secret.txt"));
        assertNull(StaticFileRenderer.resolveStaticFile(base.toFile(), "/emoji/../../latke-secret.txt"));
        assertNull(StaticFileRenderer.resolveStaticFile(base.toFile(), "/../../../../etc/passwd"));
    }

    @Test
    public void keepsAbsoluteLookupInsideBaseDirectory() throws Exception {
        final Path base = Files.createTempDirectory("latke-static");

        final File resolved = StaticFileRenderer.resolveStaticFile(base.toFile(), "/etc/passwd");

        assertNotNull(resolved);
        assertTrue(resolved.getCanonicalPath().startsWith(base.toFile().getCanonicalPath()));
        assertNotEquals(resolved.getCanonicalPath(), new File("/etc/passwd").getCanonicalPath());
    }

    @Test
    public void rejectsBlankUriAndNullByte() throws Exception {
        final Path base = Files.createTempDirectory("latke-static");

        assertNull(StaticFileRenderer.resolveStaticFile(base.toFile(), ""));
        assertNull(StaticFileRenderer.resolveStaticFile(base.toFile(), "/emoji/\0passwd"));
    }
}
