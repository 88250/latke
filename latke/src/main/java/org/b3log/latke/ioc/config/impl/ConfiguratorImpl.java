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
package org.b3log.latke.ioc.config.impl;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.bean.BeanImpl;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.config.BeanModule;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.config.InjectionPointValidator;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.util.Beans;
import org.b3log.latke.util.Reflections;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;


/**
 * Bean configurator.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Nov 26, 2009
 */
public final class ConfiguratorImpl implements Configurator {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ConfiguratorImpl.class);

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    /**
     * Modules.
     */
    private Set<BeanModule> modules;

    /**
     * &lt;BeanType, BeanClasses&gt;.
     */
    private Map<Type, Set<Class<?>>> typeClasses;

    /**
     * &lt;BeanClass, Qualifiers&gt;.
     */
    private Map<Class<?>, Set<Annotation>> classQualifiers;

    /**
     * &lt;Qualifier, BeanClasses&gt;.
     */
    private Map<Annotation, Set<Class<?>>> qualifierClasses;

    /**
     * Constructs a configurator with the specified bean manager.
     * 
     * @param beanManager the specified bean manager
     */
    public ConfiguratorImpl(final LatkeBeanManager beanManager) {
        this.beanManager = beanManager;

        typeClasses = new HashMap<Type, Set<Class<?>>>();
        classQualifiers = new HashMap<Class<?>, Set<Annotation>>();
        qualifierClasses = new HashMap<Annotation, Set<Class<?>>>();
        modules = new HashSet<BeanModule>();
    }

    @Override
    public void addTypeClassBinding(final Type beanType, final Class<?> beanClass) {
        Set<Class<?>> beanClasses = typeClasses.get(beanType);

        if (beanClasses == null) {
            beanClasses = new HashSet<Class<?>>();
        }

        beanClasses.add(beanClass);

        typeClasses.put(beanType, beanClasses);
    }

    @Override
    public void addQualifierClassBinding(final Annotation qualifier,
        final Class<?> beanClass) {
        Set<Class<?>> beanClasses = qualifierClasses.get(qualifier);

        if (beanClasses == null) {
            beanClasses = new HashSet<Class<?>>();
        }
        beanClasses.add(beanClass);

        qualifierClasses.put(qualifier, beanClasses);
    }

    @Override
    public void addClassQualifierBinding(final Class<?> beanClass,
        final Annotation qualifier) {
        Set<Annotation> qualifiers = classQualifiers.get(beanClass);

        if (null == qualifiers) {
            qualifiers = new HashSet<Annotation>();
        }

        if (qualifier.annotationType().equals(Named.class)) {
            final Iterator<Annotation> iterator = qualifiers.iterator();

            while (iterator.hasNext()) {
                if (iterator.next().annotationType().equals(Named.class)) {
                    iterator.remove(); // remove the old one
                }
            }
        }

        qualifiers.add(qualifier);
        classQualifiers.put(beanClass, qualifiers);
    }

    @Override
    public Set<Class<?>> getBindedBeanClasses(final Type beanType) {
        return typeClasses.get(beanType);
    }

    @Override
    public Set<Annotation> getBindedQualifiers(final Class<?> beanClass) {
        return classQualifiers.get(beanClass);
    }

    @Override
    public void validate() {
        for (final Bean<?> bean : beanManager.getBeans()) {
            InjectionPointValidator.checkValidity(bean);
            InjectionPointValidator.checkDependency(bean, this);
        }
    }

    @Override
    public <T> LatkeBean<T> createBean(final Class<T> beanClass) {
        try {
            return (LatkeBean<T>) beanManager.getBean(beanClass);
        } catch (final Exception e) {
            LOGGER.log(Level.TRACE, "Not found bean [beanClass={0}], so to create it", beanClass);
        }

        if (!Beans.checkClass(beanClass)) {
            throw new IllegalStateException(
                "Can't create bean for class[" + beanClass.getName()
                + "] caused by it is an interface or an abstract class, or it dose not implement any interface");
        }

        final String name = Beans.getBeanName(beanClass);

        if (null == name) {
            LOGGER.log(Level.DEBUG, "Class[beanClass={0}] can't be created as bean caused by it has no bean name.", beanClass);

            return null;
        }

        final Set<Annotation> qualifiers = Beans.getQualifiers(beanClass, name);
        final Class<? extends Annotation> scope = Beans.getScope(beanClass);
        final Set<Type> beanTypes = Beans.getBeanTypes(beanClass);
        final Set<Class<? extends Annotation>> stereotypes = Beans.getStereotypes(beanClass);

        LOGGER.log(Level.DEBUG, "Adding a bean[name={0}, scope={1}, class={2}] to the bean manager....",
            new Object[] {name, scope.getName(), beanClass.getName()});

        final LatkeBean<T> ret = new BeanImpl<T>(beanManager, name, scope, qualifiers, beanClass, beanTypes, stereotypes);

        beanManager.addBean(ret);

        for (final Type beanType : beanTypes) {
            addTypeClassBinding(beanType, beanClass);
        }

        for (final Annotation qualifier : qualifiers) {
            addClassQualifierBinding(beanClass, qualifier);
            addQualifierClassBinding(qualifier, beanClass);
        }

        return ret;
    }

    @Override
    public void createBeans(final Collection<Class<?>> classes) {
        if (null == classes || classes.isEmpty()) {
            return;
        }
        
        filterClasses(classes);

        for (final Class<?> clazz : classes) {
            createBean(clazz);
        }
    }

    @Override
    public void addModule(final BeanModule module) {
        modules.add(module);

        final Collection<Class<?>> classes = module.getBeanClasses();

        if (null != classes && !classes.isEmpty()) {
            createBeans(classes);
        }

        LOGGER.log(Level.DEBUG, "Added a module[name={0}]", module.getName());
    }

    /**
     * Filters no-bean classes with the specified classes. 
     * 
     * A valid bean is <b>NOT</b>:
     * <ul>
     *   <li>an annotation</li>
     *   <li>interface</li>
     *   <li>abstract</li>
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
