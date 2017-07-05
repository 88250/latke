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
import java.lang.reflect.Type;
import java.util.Set;

/**
 * An annotated parameter.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 30, 2010
 */
public class AnnotatedParameterImpl<T> implements AnnotatedParameter<T> {

    /**
     * Callable type of this parameter.
     */
    private AnnotatedCallable<T> annotatedCallable;

    /**
     * Parameter type.
     */
    private Type parameter;

    /**
     * Parameter position.
     */
    private int position;

    /**
     * Parameter annotations.
     */
    private Set<Annotation> annotations;

    /**
     * Constructs an annotated parameter with arguments.
     * 
     * @param annotatedCallable the specified annotated callable
     * @param parameter the specified parameter
     * @param position the specified position
     * @param annotations the specified annotations
     */
    public AnnotatedParameterImpl(final AnnotatedCallable<T> annotatedCallable,
        final Type parameter, final int position,
        final Set<Annotation> annotations) {
        this.parameter = parameter;
        this.position = position;
        this.annotations = annotations;
        this.annotatedCallable = annotatedCallable;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public AnnotatedCallable<T> getDeclaringCallable() {
        return annotatedCallable;

    }

    @Override
    public Type getBaseType() {
        return parameter;
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        return ((Class<T>) parameter).getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return ((Class<T>) parameter).isAnnotationPresent(annotationType);
    }

    @Override
    public String toString() {
        return parameter.toString() + ", position=" + position;
    }

    @Override
    public Set<Type> getTypeClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
