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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * An annotated field.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Sep 29, 2018
 * @since 2.4.18
 */
public class AnnotatedFieldImpl<T> implements AnnotatedField {

    /**
     * Field.
     */
    private final Field field;

    /**
     * Constructs an annotated field with the specified field.
     *
     * @param field the specified field
     */
    public AnnotatedFieldImpl(final Field field) {
        this.field = field;
    }

    @Override
    public Type getBaseType() {
        return field.getGenericType();
    }

    @Override
    public String toString() {
        return field.getName();
    }

    @Override
    public Field getJavaMember() {
        return field;
    }

    @Override
    public Set<AnnotatedField> getFields() {
        return null;
    }
}
