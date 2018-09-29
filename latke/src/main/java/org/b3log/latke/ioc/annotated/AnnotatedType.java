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

import java.util.Set;

/**
 * Represents a Java class or interface.
 *
 * @param <X> the type
 * @see java.lang.Class
 */
public interface AnnotatedType<X> extends Annotated {

    /**
     * Get the underlying {@link java.lang.Class}.
     *
     * @return the {@link java.lang.Class}
     */
    Class<X> getJavaClass();

    /**
     * Get the {@linkplain AnnotatedConstructor constructors} of the type.
     * If an empty set is returned, a default constructor with no parameters
     * will be assumed.
     *
     * @return the constructors, or an empty set if none are defined
     */
    Set<AnnotatedConstructor<X>> getConstructors();

    /**
     * Get the {@linkplain AnnotatedMethod methods} of the type.
     *
     * @return the methods, or an empty set if none are defined
     */
    Set<AnnotatedMethod<? super X>> getMethods();

    /**
     * Get the {@linkplain AnnotatedField fields} of the type.
     *
     * @return the fields, or an empty set if none are defined
     */
    Set<AnnotatedField<? super X>> getFields();
}
