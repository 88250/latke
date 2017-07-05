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
package org.b3log.latke.ioc;


import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.context.Context;
import org.b3log.latke.ioc.context.CreationalContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;


/**
 * Latke bean manager.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jun 20, 2013
 */
public interface LatkeBeanManager extends BeanManager {

    /**
     * Gets the bean configurator.
     *
     * @return bean configurator
     */
    Configurator getConfigurator();

    /**
     * Gets a bean with the specified bean type and qualifiers.
     *
     * @param beanType the specified bean type
     * @param qualifiers the specified qualifiers
     * @return bean
     */
    LatkeBean<?> getBean(final Type beanType, final Set<Annotation> qualifiers);

    /**
     * Gets a bean with the specified bean class.
     *
     * @param <T> tye type of bean class
     * @param beanClass the specified bean class
     * @return bean
     */
    <T> LatkeBean<T> getBean(final Class<T> beanClass);

    /**
     * Gets a bean with the specified name.
     *
     * @param name the specified name
     * @return bean
     */
    LatkeBean<?> getBean(final String name);

    /**
     * Gets a reference with the specified bean class.
     *
     * @param <T> the type of bean class
     * @param beanClass the specified bean class
     * @return reference
     */
    <T> T getReference(final Class<T> beanClass);

    /**
     * Gets a reference of the specified bean.
     *
     * @param <T> the type of bean class
     * @param bean the specified bean
     * @return reference
     */
    <T> T getReference(final LatkeBean<T> bean);

    /**
     * Gets a reference of the specified bean and creational context.
     *
     * @param bean the specified bean
     * @param creationalContext the specified creational context
     * @return reference
     */
    Object getReference(final LatkeBean<?> bean, final CreationalContext<?> creationalContext);

    /**
     * Adds the specified bean.
     *
     * @param bean the specified bean
     */
    void addBean(final LatkeBean<?> bean);

    /**
     * Gets all beans.
     *
     * @return beans
     */
    Set<LatkeBean<?>> getBeans();

    /**
     * Gets beans with the specified stereo type.
     *
     * @param stereoType the specified stereo type
     * @return beans
     */
    Set<LatkeBean<?>> getBeans(final Class<? extends Annotation> stereoType);

    /**
     * Adds the specified context.
     *
     * @param context the specified context
     */
    void addContext(final Context context);

    /**
     * Clears all contexts.
     */
    void clearContexts();
}
