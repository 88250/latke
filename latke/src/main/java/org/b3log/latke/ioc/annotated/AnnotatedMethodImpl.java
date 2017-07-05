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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * An annotated method.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 30, 2010
 */
public class AnnotatedMethodImpl<T> extends AbstractAnnotatedCallableImpl<T> implements AnnotatedMethod<T> {

    /**
     * Constructs a annotated method with the specified method.
     * 
     * @param method the specified method
     */
    public AnnotatedMethodImpl(final Method method) {
        super(method);
    }

    @Override
    public Method getJavaMember() {
        return (Method) getMember();
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Annotation> getAnnotations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return getJavaMember().getName();
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
