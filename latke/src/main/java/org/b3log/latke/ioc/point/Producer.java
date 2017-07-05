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
package org.b3log.latke.ioc.point;

import org.b3log.latke.ioc.context.CreationalContext;

import java.util.Set;


/**
 * <p>
 * Provides a generic operation for producing an instance of a type.
 * </p>
 *
 * @author Pete Muir
 * @author David Allen
 * @param <T> The class of object produced by the producer
 */
public interface Producer<T>
{
   /**
    * <p>
    * Causes an instance to be produced via the {@code Producer}.
    * </p>
    * <p>
    * If the {@code Producer} represents a class, this will invoke the
    * constructor annotated {@link javax.inject.Inject} if it exists, or the
    * constructor with no parameters otherwise. If the class has interceptors,
    * <tt>produce()</tt> is responsible for building the interceptors and
    * decorators of the instance.
    * </p>
    * <p>
    * If the {@code Producer} represents a producer field or method, this will
    * invoke the producer method on, or access the producer field of, a
    * contextual instance of the bean that declares the producer.
    * </p>
    *
    * @param ctx The {@link javax.enterprise.context.spi.CreationalContext} to
    *           use for the produced object
    * @return the instance produced
    */
   public T produce(CreationalContext<T> ctx);

   /**
    * <p>
    * Destroys the instance.
    * </p>
    * <p>
    * If the {@code Producer} represents a class, then this operation does
    * nothing.
    * </p>
    * <p>
    * If the {@code Producer} represents a producer field or method, this
    * calls the disposer method, if any, on a contextual instance of the
    * bean that declares the disposer method or performs any additional
    * required cleanup, if any, to destroy state associated with a resource.
    * </p>
    *
    * @param instance The instance to dispose
    */
   public void dispose(T instance);

   /**
    * <p>
    * Returns the set of all {@code InjectionPoints}. If the
    * {@code Producer} represents a class, then this returns returns the set of
    * {@code InjectionPoint} objects representing all injected fields, bean
    * constructor parameters and initializer method parameters. For a producer
    * method, this returns the set of {@code InjectionPoint} objects
    * representing all parameters of the producer method.
    * </p>
    *
    * @return the set of all
    *         {@linkplain javax.enterprise.inject.spi.InjectionPoint injection
    *         points} for the producer
    */
   public Set<InjectionPoint> getInjectionPoints();
}