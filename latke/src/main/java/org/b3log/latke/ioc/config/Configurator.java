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
package org.b3log.latke.ioc.config;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;
import org.b3log.latke.ioc.bean.LatkeBean;


/**
 * Bean configurator.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Mar 30, 2010
 */
public interface Configurator {

    /**
     * Creates a bean with the specified bean class.
     * 
     * @param <T> the declaring type
     * @param beanClass the specified bean class
     * @return bean
     */
    <T> LatkeBean<T> createBean(final Class<T> beanClass);

    /**
     * Creates beans with the specified bean classes.
     * 
     * @param classes the specified bean classes
     */
    void createBeans(final Collection<Class<?>> classes);

    /**
     * Adds the &lt;bean type class - bean class&gt; binding.
     * 
     * @param beanType the specified bean type
     * @param beanClass the specified bean class
     */
    void addTypeClassBinding(final Type beanType, final Class<?> beanClass);

    /**
     * Adds the &lt;bean class - qualifier&gt; binding.
     * 
     * @param beanClass the specified bean class
     * @param qualifier the specified qualifier
     */
    void addClassQualifierBinding(final Class<?> beanClass, final Annotation qualifier);

    /**
     * Adds the &lt;qualifier - bean class&gt; binding.
     * 
     * @param qualifier the specified qualifier
     * @param beanClass the specified bean class
     */
    void addQualifierClassBinding(final Annotation qualifier, final Class<?> beanClass);

    /**
     * Validates beans.
     */
    void validate();

    /**
     * Gets the binded bean classes of the specified bean type.
     * 
     * @param beanType the specified bean type
     * @return binded bean classes
     */
    Set<Class<?>> getBindedBeanClasses(final Type beanType);

    /**
     * Gets the binded qualifiers of the specified bean class.
     * 
     * @param beanClass the specified bean class
     * @return binded qualifiers 
     */
    Set<Annotation> getBindedQualifiers(final Class<?> beanClass);

    /**
     * Adds the specified module.
     * 
     * @param module the specified module
     */
    void addModule(final BeanModule module);
}
