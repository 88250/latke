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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal which compress CSS and JavaScript sources.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Sep 3, 2011
 * @goal min
 * @phase process-resources
 */
public final class MinMojo extends AbstractMojo {

    /**
     * Target directory name.
     * 
     * @parameter expression="${min.targetDirName}" default-value=""
     */
    private String targetDirName = "";
    /**
     * CSS source directory.
     * 
     * @parameter expression="${min.cssSourceDir}" default-value="${basedir}/src/main/webapp/css"
     */
    private String cssSourceDir;
    /**
     * CSS target directory.
     * 
     * @parameter expression="${min.cssTargetDir}" default-value="${project.build.directory}/${project.build.finalName}/css/"
     */
    private String cssTargetDir;
    /**
     * JavaScript source directory.
     * 
     * @parameter expression="${min.jsSourceDir}" default-value="${basedir}/src/main/webapp/js"
     */
    private String jsSourceDir;
    /**
     * JavaScript target directory.
     * 
     * @parameter expression="${min.jsTargetDir}" default-value="${project.build.directory}/${project.build.finalName}/js/"
     */
    private String jsTargetDir;
    /**
     * Admin JavaScript sources.
     * 
     * @parameter
     */
    private List<String> adminJSs = new ArrayList<String>();
    /**
     * The output filename suffix.
     * 
     * @parameter expression="${min.suffix}" default-value=""
     */
    private String suffix = "";
    // TODO: JS ONLY OPTIONS
//    /**
//     * JAVASCRIPT ONLY OPTION!<br/>
//     * Minimization only. Do not obfuscate local symbols.
//     * 
//     * @parameter expression="${min.munge}" default-value="false"
//     */
//    private boolean nomunge;
//    /**
//     * JAVASCRIPT ONLY OPTION!<br/>
//     * Display informational messages and warnings.
//     * 
//     * @parameter expression="${min.verbose}" default-value="false"
//     */
//    private boolean verbose;
//    /**
//     * JAVASCRIPT ONLY OPTION!<br/>
//     * Preserve unnecessary semicolons (such as right before a '}'). This option is useful when compressed code has to
//     * be run through JSLint (which is the case of YUI for example).
//     * 
//     * @parameter expression="${min.preserveAllSemiColons}" default-value="false"
//     */
//    private boolean preserveAllSemiColons;
//    /**
//     * JAVASCRIPT ONLY OPTION!<br/>
//     * Disable all the built-in micro optimizations.
//     * 
//     * @parameter expression="${min.disableOptimizations}" default-value="false"
//     */
//    private boolean disableOptimizations;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final ExecutorService executor = Executors.newFixedThreadPool(2);

        final Future<?> processCSSFilesTask =
                executor.submit(new CSSProcessor(getLog(),
                                                 cssSourceDir,
                                                 cssTargetDir
                                                 + targetDirName, suffix));
        final Future<?> processJSFilesTask =
                executor.submit(new JSProcessor(getLog(),
                                                jsSourceDir,
                                                jsTargetDir
                                                + targetDirName,
                                                suffix, adminJSs));

        try {
            if (processCSSFilesTask != null) {
                processCSSFilesTask.get();
            }
            if (processJSFilesTask != null) {
                processJSFilesTask.get();
            }
        } catch (final Exception e) {
            getLog().error(e.getMessage(), e);
        }
    }
}
