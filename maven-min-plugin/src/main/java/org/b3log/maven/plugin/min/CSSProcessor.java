/*
 * Copyright (c) 2011, 2012, B3log Team
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
package org.b3log.maven.plugin.min;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.maven.plugin.logging.Log;
import com.yahoo.platform.yui.compressor.CssCompressor;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Processor for compressing CSS sources.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 29, 2012
 */
public final class CSSProcessor extends SourcesProcessor {

    public CSSProcessor(final Log logger, final String srcDir, final String targetDir, final String suffix) {
        super(logger, srcDir, targetDir, suffix);
    }

    /**
     * Minimizes CSS sources.
     */
    @Override
    protected void minimize() {
        if (null == getSrcDir()) {
            getLogger().error("The source directory is null!");

            return;
        }

        try {
            final File srcDir = getSrcDir();
            final File[] srcFiles = srcDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File file) {
                    return !file.isDirectory() && !file.getName().endsWith(getSuffix() + ".css");
                }
            });

            for (int i = 0; i < srcFiles.length; i++) {
                final File src = srcFiles[i];
                final String targetPath = getTargetDir() + File.separator + src.getName().substring(0, src.getName().
                        length() - ".css".length()) + getSuffix() + ".css";
                final File target = new File(targetPath);

                getLogger().info("Minimizing [srcPath=" + src.getPath() + ", targetPath=" + targetPath + "]");
                final Reader reader = new InputStreamReader(new FileInputStream(src), "UTF-8");

                final FileOutputStream writerStream = new FileOutputStream(target);
                final Writer writer = new OutputStreamWriter(writerStream, "UTF-8");

                final CssCompressor compressor = new CssCompressor(reader);
                compressor.compress(writer, -1);

                reader.close();
                writer.close();
            }

        } catch (final IOException e) {
            getLogger().error("Minimization error!", e);
        }
    }
}
