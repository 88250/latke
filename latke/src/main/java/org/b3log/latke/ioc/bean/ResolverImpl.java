/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.ioc.bean;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.inject.Inject;
import org.b3log.latke.ioc.util.Reflections;


/**
 * Dependency resolver implementation.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Mar 30, 2010
 */
public class ResolverImpl implements Resolver {

    @Override
    public void resolveField(final AnnotatedField<?> annotatedField, final Object reference, final Object injection) {
        final Field field = annotatedField.getJavaMember();

        try {
            final Field declaredField = reference.getClass().getDeclaredField(field.getName());

            if (!Reflections.matchInheritance(declaredField, field)) {
                if (field.isAnnotationPresent(Inject.class)) {
                    final Field hideField = Reflections.getHideField(field, reference.getClass());

                    if (hideField.equals(field)) {
                        try {
                            field.set(reference, injection);
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (final NoSuchFieldException ex) {
            try {
                field.set(reference, injection);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void resolveMethod(final AnnotatedMethod<?> annotatedMethod, final Object reference, final Object[] args) {
        final Method method = annotatedMethod.getJavaMember();

        try {
            final Method declaredMethod = reference.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());

            if (!Reflections.matchInheritance(declaredMethod, method)) {
                if (method.isAnnotationPresent(Inject.class)) {
                    final Method overrideMethod = Reflections.getOverrideMethod(method, reference.getClass());

                    if (overrideMethod.equals(method)) {
                        try {
                            method.invoke(reference, args);
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (final NoSuchMethodException ex) {
            try {
                method.invoke(reference, args);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
