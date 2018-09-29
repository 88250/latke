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
package org.b3log.latke.ioc.annotated;

import org.b3log.latke.intercept.annotation.AfterMethod;
import org.b3log.latke.intercept.annotation.BeforeMethod;
import org.b3log.latke.ioc.bean.Interceptor;
import org.b3log.latke.ioc.bean.InterceptorHolder;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.util.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * An annotated type.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.7, Sep 29, 2018
 * @since 2.4.18
 */
public class AnnotatedTypeImpl<T> implements AnnotatedType<T> {

    /**
     * Bean class.
     */
    private Class<T> beanClass;

    /**
     * Annotated fields.
     */
    private Set<AnnotatedField<? super T>> annotatedFields = new HashSet<>();

    /**
     * Constructs an annotated type with the specified bean class.
     *
     * @param beanClass the bean class
     */
    public AnnotatedTypeImpl(final Class<T> beanClass) {
        this.beanClass = beanClass;
        initAnnotatedFields();
    }

    @Override
    public Set<AnnotatedField<? super T>> getFields() {
        return annotatedFields;
    }

    /**
     * Builds the annotated fields of this annotated type.
     */
    private void initAnnotatedFields() {
        final Set<Field> inheritedFields = Reflections.getInheritedFields(beanClass);
        final Set<Field> hiddenFields = Reflections.getHiddenFields(beanClass);
        final Set<Field> ownFields = Reflections.getOwnFields(beanClass);

        for (final Field field : hiddenFields) {
            final boolean isNeedToBeInjected = field.isAnnotationPresent(Inject.class);
            if (isNeedToBeInjected) {
                final AnnotatedField<T> annotatedField = new AnnotatedFieldImpl<>(field);
                field.setAccessible(true);
                annotatedFields.add(annotatedField);
            }
        }

        for (final Field field : inheritedFields) {
            final boolean isNeedToBeInjected = field.isAnnotationPresent(Inject.class);
            if (isNeedToBeInjected) {
                final AnnotatedField<T> annotatedField = new AnnotatedFieldImpl<>(field);
                field.setAccessible(true);
                annotatedFields.add(annotatedField);
            }
        }

        for (final Field field : ownFields) {
            final boolean isNeedToBeInjected = field.isAnnotationPresent(Inject.class);
            if (isNeedToBeInjected) {
                final AnnotatedField<T> annotatedField = new AnnotatedFieldImpl<>(field);
                field.setAccessible(true);
                annotatedFields.add(annotatedField);
            }
        }
    }

    @Override
    public Type getBaseType() {
        return beanClass;
    }

    /**
     * Initializes an interceptor with the specified method.
     *
     * @param method the specified method
     * @see InterceptorHolder
     */
    private void initInterceptor(final Method method) {
        final BeforeMethod beforeMethod = method.getAnnotation(BeforeMethod.class);
        if (null != beforeMethod) {
            InterceptorHolder.addInterceptor(new Interceptor(method, beforeMethod), BeforeMethod.class);
        }

        final AfterMethod afterMethod = method.getAnnotation(AfterMethod.class);
        if (null != afterMethod) {
            InterceptorHolder.addInterceptor(new Interceptor(method, afterMethod), AfterMethod.class);
        }
    }
}
