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

import java.io.File;
import org.apache.maven.plugin.logging.Log;

/**
 * Abstract sources processing task for compression (minimization).
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 3, 2011
 */
public abstract class SourcesProcessor implements Runnable {

    private Log logger;
    private File srcDir;
    private File targetDir;
    private String suffix;

    /**
     * Constructs a sources process withe the specified logger, sources 
     * directory path, target directory path and target suffix.
     * 
     * <p>
     * Creates a directory if the target directory specified by the given target 
     * directory path is inexistent.
     * </p>
     * 
     * @param logger the specified logger
     * @param srcDir the specified sources directory path
     * @param targetDir the specified target directory path
     * @param suffix the specified target suffix
     */
    public SourcesProcessor(final Log logger,
                            final String srcDir, final String targetDir,
                            final String suffix) {
        this.logger = logger;
        this.srcDir = new File(srcDir);
        this.targetDir = new File(targetDir);
        if (!this.targetDir.exists()) {
            this.targetDir.mkdirs();
        }

        this.suffix = suffix;
    }

    @Override
    public void run() {
        minimize();
    }

    public Log getLogger() {
        return logger;
    }

    public File getSrcDir() {
        return srcDir;
    }

    public String getSuffix() {
        return suffix;
    }

    public File getTargetDir() {
        return targetDir;
    }

    /**
     * Minimizes source file.
     */
    protected abstract void minimize();
}
