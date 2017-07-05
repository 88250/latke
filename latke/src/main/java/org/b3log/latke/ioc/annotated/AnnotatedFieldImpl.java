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
package org.b3log.latke.ioc.annotated;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * An annotated field.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Mar 30, 2010
 */
public class AnnotatedFieldImpl<T> implements AnnotatedField<T> {

    /**
     * Field.
     */
    private Field field;

    /**
     * Constructs an annotated field with the specified field.
     * 
     * @param field the specified field
     */
    public AnnotatedFieldImpl(final Field field) {
        this.field = field;
    }

    @Override
    public Field getJavaMember() {
        return field;
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    @Override
    public Type getBaseType() {
        return field.getGenericType();
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        return field.getAnnotation(annotationType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Annotation> getAnnotations() {
        return new HashSet<Annotation>(Arrays.asList(field.getAnnotations()));
    }

    @Override
    public boolean isAnnotationPresent(
        final Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    @Override
    public String toString() {
        return field.getName();
    }

    @Override
    public Set<Type> getTypeClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
