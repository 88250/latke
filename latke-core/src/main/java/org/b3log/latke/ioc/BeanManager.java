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

import org.b3log.latke.event.EventManager;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.advice.ProcessAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Latke bean manager implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.0, Sep 29, 2018
 * @since 2.4.18
 */
@Singleton
public class BeanManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BeanManager.class);

    /**
     * Built-in beans.
     */
    private static Set<Bean<?>> builtInBeans;

    /**
     * Built-in bean classes.
     */
    private static List<Class<?>> builtInBeanClasses = Arrays.asList(
            LangPropsService.class,
            ProcessAdvice.class,
            EventManager.class,
            PluginManager.class);

    /**
     * Configurator.
     */
    private Configurator configurator;

    /**
     * Beans.
     */
    private Set<Bean<?>> beans;

    /**
     * Context.
     */
    private SingletonContext context;

    /**
     * Constructs a Latke bean manager.
     */
    private BeanManager() {
        LOGGER.log(Level.DEBUG, "Creating bean manager");

        beans = new HashSet<>();
        context = new SingletonContext();
        builtInBeans = new HashSet<>();
        configurator = new Configurator(this);
        configurator.createBean(BeanManager.class);

        for (final Class<?> builtInBeanClass : builtInBeanClasses) {
            final Bean<?> builtInBean = configurator.createBean(builtInBeanClass);
            builtInBeans.add(builtInBean);
            context.get(builtInBean);
        }

        beans.addAll(builtInBeans);

        LOGGER.log(Level.DEBUG, "Created Latke bean manager");
    }

    /**
     * Starts the application with the specified bean class and bean modules.
     *
     * @param classes the specified bean class, nullable
     */
    public static void start(final Collection<Class<?>> classes) {
        LOGGER.log(Level.DEBUG, "Initializing Latke IoC container");

        final Configurator configurator = getInstance().getConfigurator();
        if (null != classes && !classes.isEmpty()) {
            configurator.createBeans(classes);
        }

        LOGGER.log(Level.DEBUG, "Initialized Latke IoC container");
    }

    /**
     * Ends the application.
     */
    public static void close() {
        LOGGER.log(Level.DEBUG, "Closed Latke IoC container");
    }

    public static BeanManager getInstance() {
        return BeanManagerHolder.instance;
    }

    public void addBean(final Bean<?> bean) {
        beans.add(bean);
    }

    public Set<Bean<?>> getBeans(final Class<? extends Annotation> stereoType) {
        final Set<Bean<?>> ret = new HashSet<>();

        for (final Bean<?> bean : beans) {
            final Set<Class<? extends Annotation>> stereotypes = bean.getStereotypes();

            if (stereotypes.contains(stereoType)) {
                ret.add(bean);
            }
        }

        return ret;
    }


    public Configurator getConfigurator() {
        return configurator;
    }

    public <T> T getReference(final Bean<T> bean) {
        return context.get(bean);
    }

    public Object getInjectableReference(final InjectionPoint ij) {
        final Type baseType = ij.getAnnotated().getBaseType();
        final Bean<?> bean = getBean(baseType);

        return getReference(bean);
    }

    public <T> Bean<T> getBean(final Class<T> beanClass) {
        for (final Bean<?> bean : beans) {
            if (bean.getBeanClass().equals(beanClass)) {
                return (Bean<T>) bean;
            }
        }

        throw new RuntimeException("Not found bean [beanClass=" + beanClass.getName() + ']');
    }

    private <T> Bean<T> getBean(final Type beanType) {
        for (final Bean<?> bean : beans) {
            if (bean.getBeanClass().equals(beanType)) {
                return (Bean<T>) bean;
            }
        }

        throw new RuntimeException("Not found bean [beanType=" + beanType + "]");
    }


    public <T> T getReference(final Class<T> beanClass) {
        final Bean<T> bean = getBean(beanClass);

        return getReference(bean);
    }

    /**
     * Root bean manager holder.
     */
    private static final class BeanManagerHolder {

        /**
         * Singleton of bean manager.
         */
        private static BeanManager instance = new BeanManager();

        /**
         * Private constructor.
         */
        private BeanManagerHolder() {
        }
    }
}