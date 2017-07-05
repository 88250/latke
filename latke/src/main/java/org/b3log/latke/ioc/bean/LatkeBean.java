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


import org.b3log.latke.ioc.BeanManager;

import java.lang.annotation.Annotation;

/**
 * B3log Latke bean model.
 * <p>
 * <p>Adds some setters for a {@link Bean}.
 *
 * @param <T>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Mar 30, 2010
 */
public interface LatkeBean<T> extends Bean<T> {

    /**
     * Sets a scope of this bean with the specified scope.
     *
     * @param scope the specified scope
     * @return this bean
     */
    LatkeBean<T> scoped(final Class<? extends Annotation> scope);

    /**
     * Sets qualifiers of this bean with the specified qualifiers.
     *
     * @param qualifier  the specified qualifier
     * @param qualifiers the specified qualifiers
     * @return this bean
     */
    LatkeBean<T> qualified(final Annotation qualifier, final Annotation... qualifiers);

    /**
     * Sets the name of this bean with the specified name.
     *
     * @param name the specified name
     * @return this bean
     */
    LatkeBean<T> named(final String name);

    /**
     * Gets the bean manager.
     *
     * @return the bean manager
     */
    BeanManager getBeanManager();
}
