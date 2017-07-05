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
package org.b3log.latke.ioc.bean;

import org.b3log.latke.ioc.context.Contextual;
import org.b3log.latke.ioc.point.InjectionPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * <p>Represents an {@linkplain javax.enterprise.inject enabled bean}. This
 * interface defines everything the container needs to manage instances of
 * the bean.</p>
 *
 * @author Gavin King
 * @author David Allen
 * @param <T> the class of the bean instance
 */
public interface Bean<T> extends Contextual<T>
{

    /**
     * Obtains the {@linkplain javax.enterprise.inject bean types} of the bean.
     *
     * @return the {@linkplain javax.enterprise.inject bean types}
     */
    public Set<Type> getTypes();

    /**
     * Obtains the {@linkplain javax.inject.Qualifier qualifiers} of the bean.
     *
     * @return the {@linkplain javax.inject.Qualifier qualifiers}
     */
    public Set<Annotation> getQualifiers();

    /**
     * Obtains the {@linkplain javax.enterprise.context scope} of the bean.
     *
     * @return the {@linkplain javax.enterprise.context scope}
     */
    public Class<? extends Annotation> getScope();

    /**
     * Obtains the {@linkplain javax.enterprise.inject EL name} of a bean, if it has one.
     *
     * @return the {@linkplain javax.enterprise.inject EL name}
     */
    public String getName();

    /**
     * Obtains the {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     * of the bean.
     *
     * @return the set of {@linkplain javax.enterprise.inject.Stereotype stereotypes}
     */
    public Set<Class<? extends Annotation>> getStereotypes();

    /**
     * The bean {@linkplain Class class} of the managed bean or session bean or
     * of the bean that declares the producer method or field.
     *
     * @return the bean {@linkplain Class class}
     */
    public Class<?> getBeanClass();

    /**
     * Determines if the bean is an
     * {@linkplain javax.enterprise.inject.Alternative alternative}.
     *
     * @return <tt>true</tt> if the bean is an
     *         {@linkplain javax.enterprise.inject.Alternative alternative},
     *         and <tt>false</tt> otherwise.
     */
    public boolean isAlternative();

    /**
     * Determines if
     * {@link Contextual#create(CreationalContext)}
     * sometimes return a null value.
     *
     * @return <tt>true</tt> if the {@code create()} method may return a null
     *        value, and <tt>false</tt> otherwise
     */
    public boolean isNullable();

    /**
     * Obtains the {@link javax.enterprise.inject.spi.InjectionPoint} objects
     * representing injection points of the bean, that will be validated by the
     * container at initialization time.
     *
     * @return the set of {@linkplain javax.enterprise.inject.spi.InjectionPoint injection points} of the bean
     */
    public Set<InjectionPoint> getInjectionPoints();

}
