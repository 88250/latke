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
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * An annotated constructor.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 30, 2010
 */
public class AnnotatedConstructorImpl<T> extends AbstractAnnotatedCallableImpl<T> implements
        AnnotatedConstructor<T> {

    /**
     * Constructs an annotated constructor with the specified constructor.
     *
     * @param constructor the specified constructor
     */
    public AnnotatedConstructorImpl(final Constructor<T> constructor) {
        super(constructor);
    }

    @Override
    public Constructor<T> getJavaMember() {
        return (Constructor) getMember();
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        return getJavaMember().getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return new HashSet<Annotation>(Arrays.asList(getJavaMember().getAnnotations()));
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return getJavaMember().isAnnotationPresent(annotationType);
    }

    @Override
    public Type getBaseType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Type> getTypeClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
