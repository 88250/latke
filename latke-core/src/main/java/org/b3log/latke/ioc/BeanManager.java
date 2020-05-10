/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.ioc;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.service.LangPropsService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Latke bean manager implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Nov 3, 2019
 * @since 2.4.18
 */
@Singleton
public class BeanManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(BeanManager.class);

    /**
     * Built-in beans.
     */
    private static Set<Bean<?>> builtInBeans;

    /**
     * Built-in bean classes.
     */
    private static final List<Class<?>> builtInBeanClasses = Arrays.asList(
            LangPropsService.class,
            EventManager.class,
            PluginManager.class);

    /**
     * Configurator.
     */
    private final Configurator configurator;

    /**
     * Beans.
     */
    private final Set<Bean<?>> beans;

    /**
     * Context.
     */
    private final SingletonContext context;

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
            if (bean.getTypes().contains(beanType)) {
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
        private static final BeanManager instance = new BeanManager();

        /**
         * Private constructor.
         */
        private BeanManagerHolder() {
        }
    }
}