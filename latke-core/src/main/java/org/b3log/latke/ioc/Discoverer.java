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
package org.b3log.latke.ioc;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.util.AntPathMatcher;
import org.b3log.latke.util.ArrayUtils;

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
 * @version 1.0.0.5, Sep 29, 2018
 * @since 2.4.18
 */
public final class Discoverer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Discoverer.class);

    /**
     * Built-in component packages.
     */
    private static final String[] BUILT_IN_COMPONENT_PKGS = new String[]{"org.b3log.latke.remote"};

    /**
     * Private constructor.
     */
    private Discoverer() {
    }

    /**
     * Scans classpath to discover bean classes.
     *
     * @param scanPath the paths to scan, using ',' as the separator. There are two types of the scanPath:
     *                 <ul>
     *                 <li>package: org.b3log.process</li>
     *                 <li>ant-style classpath: org/b3log/** /*process.class</li>
     *                 </ul>
     * @return discovered classes
     * @throws Exception exception
     */
    public static Collection<Class<?>> discover(final String scanPath) throws Exception {
        if (StringUtils.isBlank(scanPath)) {
            throw new IllegalStateException("Please specify the [scanPath]");
        }

        LOGGER.debug("scanPath[" + scanPath + "]");

        final Collection<Class<?>> ret = new HashSet<>();
        final String[] splitPaths = scanPath.split(",");

        // Adds some built-in components
        final String[] paths = ArrayUtils.concatenate(splitPaths, BUILT_IN_COMPONENT_PKGS);
        final Set<URL> urls = new LinkedHashSet<>();
        for (String path : paths) {
            /*
             * the being two types of the scanPath.
             *  1 package: org.b3log.xxx
             *  2 ant-style classpath: org/b3log/** /*process.class
             */
            if (!AntPathMatcher.isPattern(path)) {
                path = path.replaceAll("\\.", "/") + "/**/*.class";
            }

            urls.addAll(ClassPathResolver.getResources(path));
        }

        for (final URL url : urls) {
            final DataInputStream classInputStream = new DataInputStream(url.openStream());
            final ClassFile classFile = new ClassFile(classInputStream);
            final String className = classFile.getName();

            final AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
            if (null == annotationsAttribute) {
                LOGGER.log(Level.TRACE, "The class [name={0}] is not a bean", className);

                continue;
            }

            final ConstPool constPool = classFile.getConstPool();
            final Annotation[] annotations = annotationsAttribute.getAnnotations();
            boolean maybeBeanClass = false;
            for (final Annotation annotation : annotations) {
                final String typeName = annotation.getTypeName();
                if (typeName.equals(Singleton.class.getName())) {
                    maybeBeanClass = true;

                    break;
                }

                if (typeName.equals(RequestProcessor.class.getName()) || typeName.equals(Service.class.getName()) ||
                        typeName.equals(Repository.class.getName())) {
                    final Annotation singletonAnnotation = new Annotation(Singleton.class.getName(), constPool);
                    annotationsAttribute.addAnnotation(singletonAnnotation);
                    classFile.addAttribute(annotationsAttribute);
                    classFile.setVersionToJava5();
                    maybeBeanClass = true;

                    break;
                }
            }

            if (maybeBeanClass) {
                Class<?> clz = null;
                try {
                    clz = Thread.currentThread().getContextClassLoader().loadClass(className);
                } catch (final ClassNotFoundException e) {
                    LOGGER.log(Level.ERROR, "Loads class [" + className + "] failed", e);
                }

                ret.add(clz);
            }
        }

        return ret;
    }
}
