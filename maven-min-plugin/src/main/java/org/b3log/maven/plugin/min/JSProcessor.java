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

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

/**
 * Processor for compressing JavaScript sources.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Aug 29, 2012
 */
public class JSProcessor extends SourcesProcessor {

    private boolean munge;
    private boolean verbose;
    private boolean preserveAllSemiColons;
    private boolean disableOptimizations;
    private List<String> adminJSs;

    public JSProcessor(final Log logger, final String srcDir, final String targetDir, final String suffix, final List<String> adminJSs) {
        super(logger, srcDir, targetDir, suffix);
        this.adminJSs = adminJSs;
    }

    /**
     * Minimizes JavaScript sources.
     */
    @Override
    protected void minimize() {
        if (null == getSrcDir()) {
            getLogger().error("The source directory is null!");

            return;
        }

        try {
            final File srcDir = getSrcDir();
            processAdminJS(srcDir);

            final File[] srcFiles = srcDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(final File file) {
                    return !file.isDirectory() && !file.getName().endsWith(getSuffix() + ".js");
                }
            });

            for (int i = 0; i < srcFiles.length; i++) {
                final File src = srcFiles[i];
                final String targetPath = getTargetDir() + File.separator + src.getName().substring(0, src.getName().
                        length() - ".js".length()) + getSuffix() + ".js";
                final File target = new File(targetPath);

                getLogger().info("Minimizing [srcPath=" + src.getPath() + ", targetPath=" + targetPath + "]");
                final Reader reader = new InputStreamReader(new FileInputStream(src), "UTF-8");

                final FileOutputStream writerStream = new FileOutputStream(target);
                final Writer writer = new OutputStreamWriter(writerStream, "UTF-8");

                final JavaScriptCompressor compressor = new JavaScriptCompressor(
                        reader, new JavaScriptErrorReporter(getLogger(), src.getName()));
                compressor.compress(writer, -1, munge, verbose, preserveAllSemiColons, disableOptimizations);

                reader.close();
                writer.close();
            }
        } catch (final Exception e) {
            getLogger().error("Minimization error!", e);
        }
    }

    private void processAdminJS(final File srcDir) throws Exception {
        if (adminJSs.isEmpty()) {
            return;
        }

        final File adminDir = new File(srcDir + File.separator + "admin");

        final List<File> adminJSList = new ArrayList<File>();
        for (final String adminJS : adminJSs) {
            final File adminJSFile = new File(adminDir.getPath() + File.separator + adminJS);
            adminJSList.add(adminJSFile);
        }

        final File latkeAdminJS = merge(adminJSList);

        final File latkeMinAdminJS =
                new File(getTargetDir() + File.separator + "admin" + File.separator + "latkeAdmin" + getSuffix() + ".js");

        getLogger().info("Minimizing [srcPath=" + latkeAdminJS.getPath() + ", targetPath=" + latkeMinAdminJS.getPath() + "]");

        final Reader reader = new InputStreamReader(
                new FileInputStream(latkeAdminJS), "UTF-8");
        final FileOutputStream writerStream = new FileOutputStream(latkeMinAdminJS);
        final Writer writer = new OutputStreamWriter(writerStream, "UTF-8");

        final JavaScriptCompressor compressor = new JavaScriptCompressor(
                reader, new JavaScriptErrorReporter(getLogger(), latkeAdminJS.getName()));
        compressor.compress(writer, -1, munge, verbose, preserveAllSemiColons, disableOptimizations);

        reader.close();
        writer.close();
    }

    private File merge(final List<File> files) throws Exception {
        if (files.isEmpty()) {
            return null;
        }

        final File ret = new File(getTargetDir() + File.separator + "admin" + File.separator + "latkeAdmin.js");
        final File adminTargetDir = new File(getTargetDir() + File.separator + "admin");
        if (!adminTargetDir.exists()) {
            adminTargetDir.mkdir();
        }

        final FileOutputStream fileOutputStream = new FileOutputStream(ret);

        final StringBuilder sb = new StringBuilder("Merged [\r\n  ");
        for (int i = 0; i < files.size(); i++) {
            final FileInputStream fileInputStream = new FileInputStream(files.get(i));
            IOUtil.copy(fileInputStream, fileOutputStream);

            fileInputStream.close();

            sb.append(files.get(i).getPath());

            if (i < files.size() - 1) {
                sb.append(",\r\n  ");
            }
        }
        sb.append("\r\n], [").append(files.size()).append("] files");

        getLogger().info(sb.toString());

        fileOutputStream.close();

        return ret;
    }
}
