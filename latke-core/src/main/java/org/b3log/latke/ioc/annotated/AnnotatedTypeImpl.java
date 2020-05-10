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
package org.b3log.latke.ioc.annotated;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.util.Reflections;

import java.lang.reflect.Field;
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
    private final Class<T> beanClass;

    /**
     * Annotated fields.
     */
    private final Set<AnnotatedField<? super T>> annotatedFields = new HashSet<>();

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
        final Set<Field> hiddenFields = Reflections.getHiddenFields(beanClass);
        inject(hiddenFields);

        final Set<Field> inheritedFields = Reflections.getInheritedFields(beanClass);
        inject(inheritedFields);

        final Set<Field> ownFields = Reflections.getOwnFields(beanClass);
        inject(ownFields);
    }

    private void inject(Set<Field> fields) {
        for (final Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
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
}
