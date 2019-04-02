/*
 * Copyright (c) 2009-present, b3log.org
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

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Bean configurator.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Sep 30, 2018
 * @since 2.4.18
 */
public class Configurator {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Configurator.class);

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    /**
     * &lt;BeanType, BeanClasses&gt;.
     */
    private Map<Type, Set<Class<?>>> typeClasses;

    /**
     * Constructs a configurator with the specified bean manager.
     *
     * @param beanManager the specified bean manager
     */
    public Configurator(final BeanManager beanManager) {
        this.beanManager = beanManager;
        typeClasses = new HashMap<>();
    }

    public void addTypeClassBinding(final Type beanType, final Class<?> beanClass) {
        Set<Class<?>> beanClasses = typeClasses.get(beanType);
        if (null == beanClasses) {
            beanClasses = new HashSet<>();
        }

        beanClasses.add(beanClass);
        typeClasses.put(beanType, beanClasses);
    }

    public <T> Bean<T> createBean(final Class<T> beanClass) {
        try {
            return beanManager.getBean(beanClass);
        } catch (final Exception e) {
            LOGGER.log(Level.TRACE, "Not found bean [beanClass={0}], so to create it", beanClass);
        }

        if (Reflections.isAbstract(beanClass) || Reflections.isInterface(beanClass)) {
            throw new IllegalStateException("Can't create bean for class [" + beanClass.getName() + "] caused by it is an interface or an abstract class, or it dose not implement any interface");
        }

        final String className = beanClass.getName();
        final String name = className.substring(0, 1).toLowerCase() + className.substring(1);
        final Set<Type> beanTypes = Reflections.getBeanTypes(beanClass);
        final Set<Class<? extends Annotation>> stereotypes = Reflections.getStereotypes(beanClass);

        LOGGER.log(Level.DEBUG, "Adding a bean [name={0}, class={1}] to the bean manager", name, beanClass.getName());

        final Bean<T> ret = new Bean<T>(beanManager, name, beanClass, beanTypes, stereotypes);
        beanManager.addBean(ret);

        for (final Type beanType : beanTypes) {
            addTypeClassBinding(beanType, beanClass);
        }

        return ret;
    }

    public void createBeans(final Collection<Class<?>> classes) {
        if (null == classes || classes.isEmpty()) {
            return;
        }

        filterClasses(classes);

        for (final Class<?> clazz : classes) {
            createBean(clazz);
        }
    }

    /**
     * Filters no-bean classes with the specified classes.
     * <p>
     * A valid bean is <b>NOT</b>:
     * <ul>
     * <li>an annotation</li>
     * <li>interface</li>
     * <li>abstract</li>
     * </ul>
     *
     * @param classes the specified classes to filter
     */
    private static void filterClasses(final Collection<Class<?>> classes) {
        final Iterator<Class<?>> iterator = classes.iterator();
        while (iterator.hasNext()) {
            final Class<?> clazz = iterator.next();
            if (clazz.isAnnotation() || !Reflections.isConcrete(clazz)) {
                iterator.remove();
            }
        }
    }
}
