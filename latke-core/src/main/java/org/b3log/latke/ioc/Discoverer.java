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
package org.b3log.latke.ioc;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.latke.service.annotation.Service;
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
 * @version 1.0.0.7, Feb 9, 2020
 * @since 2.4.18
 */
public final class Discoverer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Discoverer.class);

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
     * @param scanPath the package paths to scan, using ',' as the separator, for example "org.b3log,com.hacpai"
     * @return discovered classes
     */
    public static Collection<Class<?>> discover(final String scanPath) {
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
            path = path.replaceAll("\\.", "/") + "/**/*.class";
            urls.addAll(ClassPathResolver.getResources(path));
        }

        try {
            for (final URL url : urls) {
                final DataInputStream classInputStream = new DataInputStream(url.openStream());
                final ClassFile classFile = new ClassFile(classInputStream);
                final String className = classFile.getName();

                final AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
                if (null == annotationsAttribute) {
                    LOGGER.log(Level.TRACE, "The class [name={}] is not a bean", className);
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

                    if (typeName.equals(Service.class.getName()) || typeName.equals(Repository.class.getName())) {
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
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Load classes failed", e);
        }

        return ret;
    }
}
