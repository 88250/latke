/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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
package org.b3log.latke.ioc.config;


import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.servlet.ClassPathResolver;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.AntPathMatcher;
import org.b3log.latke.util.ArrayUtils;
import org.b3log.latke.util.Strings;

import java.io.DataInputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Bean discoverer.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Mar 30, 2010
 */
public final class Discoverer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Discoverer.class);

    /**
     * Built-in component packages.
     */
    private static final String[] BUILT_IN_COMPONENT_PKGS = new String[] {"org.b3log.latke.remote"};

    /**
     * Private constructor.
     */
    private Discoverer() {}

    /**
     * Scans classpath to discover bean classes.
     * 
     * @param scanPath the paths to scan, using ',' as the separator. There are two types of the scanPath: 
     * <ul>
     *   <li>package: org.b3log.process</li>
     *   <li>ant-style classpath: org/b3log/** /*process.class</li>
     * </ul>
     * @return discovered classes
     * @throws Exception exception
     */
    public static Collection<Class<?>> discover(final String scanPath) throws Exception {
        if (Strings.isEmptyOrNull(scanPath)) {
            throw new IllegalStateException("Please specify the [scanPath]");
        }
        
        LOGGER.debug("scanPath[" + scanPath + "]");

        // See issue #17 (https://github.com/b3log/latke/issues/17) for more details

        final Collection<Class<?>> ret = new HashSet<Class<?>>();

        final String[] splitPaths = scanPath.split(",");

        // Adds some built-in components
        final String[] paths = ArrayUtils.concatenate(splitPaths, BUILT_IN_COMPONENT_PKGS);

        final Set<URL> urls = new LinkedHashSet<URL>();

        for (String path : paths) {

            /*
             * the being two types of the scanPath.
             *  1 package: org.b3log.process
             *  2 ant-style classpath: org/b3log/** /*process.class
             */
            if (!AntPathMatcher.isPattern(path)) {
                path = path.replaceAll("\\.", "/") + "/**/*.class";
            }

            urls.addAll(ClassPathResolver.getResources(path));
        }

        for (URL url : urls) {
            final DataInputStream classInputStream = new DataInputStream(url.openStream());

            final ClassFile classFile = new ClassFile(classInputStream);
            final String className = classFile.getName();

            final AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);

            if (null == annotationsAttribute) {
                LOGGER.log(Level.TRACE, "The class[name={0}] is not a bean", className);

                continue;
            }

            final ConstPool constPool = classFile.getConstPool();

            final Annotation[] annotations = annotationsAttribute.getAnnotations();

            boolean maybeBeanClass = false;

            for (final Annotation annotation : annotations) {
                if (annotation.getTypeName().equals(RequestProcessor.class.getName())) {
                    // Request Processor is singleton scoped
                    final Annotation singletonAnnotation = new Annotation("javax.inject.Singleton", constPool);

                    annotationsAttribute.addAnnotation(singletonAnnotation);
                    classFile.addAttribute(annotationsAttribute);
                    classFile.setVersionToJava5();

                    maybeBeanClass = true;

                    break;
                }

                if (annotation.getTypeName().equals(Service.class.getName())
                    || (annotation.getTypeName()).equals(Repository.class.getName())) {
                    // Service and Repository is singleton scoped by default
                    maybeBeanClass = true;

                    break;
                }
                
                if (annotation.getTypeName().equals(Named.class.getName())) {
                    // Annoatated with Named maybe a bean class
                    maybeBeanClass = true;
                    
                    break;
                }
                
                // Others will not load
            }

            if (maybeBeanClass) {
                Class<?> clz = null;

                try {
                    clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                } catch (final ClassNotFoundException e) {
                    LOGGER.log(Level.ERROR, "some error to load the class[" + className + "]", e);
                }

                ret.add(clz);
            }
        }

        return ret;
    }
}
