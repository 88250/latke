/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.ioc.util;

import org.b3log.latke.ioc.inject.Scope;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.ioc.inject.Stereotype;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean utilities.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Sep 29, 2018
 * @since 2.4.18
 */
public final class Beans {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Beans.class);

    /**
     * Private constructor.
     */
    private Beans() {
    }

    /**
     * Gets scope of the specified class.
     *
     * @param clazz the specified class
     * @return scope of the specified class, returns {@link Singleton} class as the default scope of the specified class
     */
    public static Class<? extends Annotation> getScope(final Class<?> clazz) {
        final Set<Annotation> annotations = CollectionUtils.arrayToSet(clazz.getAnnotations());
        final Set<Annotation> ret = getAnnotations(annotations, Scope.class);

        if (!ret.isEmpty() && ret.size() != 1) {
            throw new RuntimeException("A bean class can only has one scope!");
        }

        return ret.isEmpty() ? Singleton.class : ret.iterator().next().annotationType();
    }

    /**
     * Gets stereo types of the specified class.
     *
     * @param clazz the specified class
     * @return stereo types of the specified class
     */
    public static Set<Class<? extends Annotation>> getStereotypes(final Class<?> clazz) {
        final Set<Class<? extends Annotation>> ret = new HashSet<Class<? extends Annotation>>();

        final Set<Annotation> annotations = getAnnotations(CollectionUtils.arrayToSet(clazz.getAnnotations()), Stereotype.class);

        if (annotations.isEmpty()) {
            return ret;
        }

        for (final Annotation annotation : annotations) {
            ret.add(annotation.annotationType());
        }

        return ret;
    }

    public static final String getBeanName(final Class<?> clazz) {
        final String className = clazz.getName();

        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    /**
     * Determines whether the specified could create bean.
     *
     * @param clazz the specified class
     * @return {@code true} if it could create bean, returns {@code false} otherwise
     */
    public static boolean checkClass(final Class<?> clazz) {
        if (Reflections.isAbstract(clazz) || Reflections.isInterface(clazz)) {
            return false;
        }

        return true;
    }

    public static final <T> Set<Type> getBeanTypes(final Class<T> beanClass) {
        final Set<Type> ret = new HashSet<Type>();

        ret.add(beanClass);
        Type genericSuperclass = beanClass;

        while (genericSuperclass != Object.class) {
            Type[] genericInterfaces = null;

            if (genericSuperclass instanceof Class<?>) {
                genericInterfaces = ((Class<?>) genericSuperclass).getGenericInterfaces();
                genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
            } else if (genericSuperclass instanceof ParameterizedType) {
                final Type rawType = ((ParameterizedType) genericSuperclass).getRawType();

                genericInterfaces = ((Class<?>) rawType).getGenericInterfaces();
                genericSuperclass = ((Class<?>) rawType).getGenericSuperclass();
            }

            if (genericSuperclass != Object.class) {
                ret.add(genericSuperclass);
            }

            if (null != genericInterfaces && 0 != genericInterfaces.length) {
                for (final Type genericInterface : genericInterfaces) {
                    ret.add(genericInterface);
                    ret.addAll(getInterfaces((Class<? super T>) genericInterface));
                }
            }
        }

        return ret;
    }

    private static final <T> Set<Type> getInterfaces(final Class<T> interfaceClass) {
        final Set<Type> ret = new HashSet<Type>();
        final Class<?>[] interfaces = interfaceClass.getInterfaces();

        if (interfaces.length == 0) {
            return ret;
        } else {
            for (final Class<?> i : interfaces) {
                ret.add(i);
                ret.addAll(getInterfaces(i));
            }

            return ret;
        }
    }

    public static final Set<Class<? extends Annotation>> toAnnotationTypes(final Set<Annotation> annotations) {
        final Set<Class<? extends Annotation>> ret = new HashSet<Class<? extends Annotation>>();

        for (final Annotation beanQualifier : annotations) {
            ret.add(beanQualifier.annotationType());
        }

        return ret.isEmpty() ? null : ret;
    }

    /**
     * Gets annotations match the needed annotation type from the specified annotation.
     *
     * @param annotations          the specified annotations
     * @param neededAnnotationType the needed annotation type
     * @return annotation set, returns an empty set if not found
     */
    private static Set<Annotation> getAnnotations(final Set<Annotation> annotations,
                                                  final Class<? extends Annotation> neededAnnotationType) {
        final Set<Annotation> ret = new HashSet<Annotation>();

        for (final Annotation annotation : annotations) {
            annotation.annotationType().getAnnotations();
            final Annotation[] metaAnnotations = annotation.annotationType().getAnnotations();

            for (final Annotation metaAnnotation : metaAnnotations) {
                if (metaAnnotation.annotationType().equals(neededAnnotationType)) {
                    ret.add(annotation);
                }
            }
        }

        return ret;
    }
}
