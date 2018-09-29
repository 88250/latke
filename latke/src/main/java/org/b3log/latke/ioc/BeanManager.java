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
package org.b3log.latke.ioc;

import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.context.*;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.ioc.point.InjectionPoint;
import org.b3log.latke.ioc.util.Beans;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.service.LangPropsServiceImpl;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.util.Reflections;

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
@Named
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
            LangPropsServiceImpl.class,
            BeforeRequestProcessAdvice.class,
            AfterRequestProcessAdvice.class,
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
     * Contexts.
     */
    private Map<Class<? extends Annotation>, Set<Context>> contexts;

    /**
     * Constructs a Latke bean manager.
     */
    private BeanManager() {
        LOGGER.log(Level.DEBUG, "Creating Latke bean manager");

        beans = new HashSet<>();
        contexts = new HashMap<>();
        builtInBeans = new HashSet<>();
        configurator = new Configurator(this);

        // Init Singleton context
        final SingletonContext singletonContext = new SingletonContext();
        final Bean<BeanManager> beanManagerBean = configurator.createBean(BeanManager.class);

        singletonContext.add(beanManagerBean, this);
        singletonContext.setActive(true);
        addContext(singletonContext);

        // Constructs the built-in beans with singleton
        for (final Class<?> builtInBeanClass : builtInBeanClasses) {
            final Bean<?> builtInBean = configurator.createBean(builtInBeanClass);

            builtInBeans.add(builtInBean);
            singletonContext.get(builtInBean, new CreationalContextImpl(builtInBean));
        }

        beans.addAll(builtInBeans);

        LOGGER.log(Level.DEBUG, "Created Latke bean manager");
    }

    public static BeanManager getInstance() {
        return BeanManagerHolder.instance;
    }


    public void addBean(final Bean<?> bean) {
        beans.add(bean);
    }


    public Set<Bean<?>> getBeans() {
        return beans;
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

    public <T> Bean<T> getBean(final Class<T> beanClass) {
        for (final Bean<?> bean : beans) {
            if (bean.getBeanClass().equals(beanClass)) {
                return (Bean<T>) bean;
            }
        }

        throw new RuntimeException("Can not get bean with class [" + beanClass.getName() + ']');
    }

    public Configurator getConfigurator() {
        return configurator;
    }

    public Object getReference(final Bean bean, final Type beanType, final CreationalContext ctx) {
        final Context activeContext = getContext(bean.getScope());

        return activeContext.get(bean, ctx);
    }

    public Object getInjectableReference(final InjectionPoint ij, final CreationalContext<?> ctx) {
        final Type baseType = ij.getAnnotated().getBaseType();
        final Set<Annotation> requiredQualifiers = ij.getQualifiers();
        final Bean<?> bean = getBean(baseType, requiredQualifiers);
        Object ret;

        if (bean.getScope() != Singleton.class) {
            ret = bean.create(null);
        } else {
            ret = getReference(bean, baseType, ctx);
        }

        return ret;
    }

    public Set<Bean<?>> getBeans(final String name) {
        final Set<Bean<?>> ret = new HashSet<>();

        for (final Bean<?> bean : beans) {
            if (bean.getName().equals(name)) {
                ret.add(bean);
            }
        }

        return ret;
    }

    public void addContext(final Context context) {
        final Class<? extends Annotation> scope = context.getScope();
        Set<Context> contextSet = contexts.get(scope);

        if (contextSet == null) {
            contextSet = new HashSet<>();
        }
        contextSet.add(context);

        contexts.put(scope, contextSet);
    }

    public Context getContext(final Class<? extends Annotation> scopeType) {
        final Set<Context> contextSet = contexts.get(scopeType);
        final Set<Context> ret = new HashSet<>();

        if (contextSet != null) {
            for (final Context context : contextSet) {
                if (context.isActive()) {
                    ret.add(context);
                }
            }
        }

        if (ret.isEmpty()) {
            throw new ContextNotActiveException("Has no active context for scope[name=" + scopeType.getName() + "]");
        }

        if (ret.size() > 1) {
            throw new IllegalArgumentException("There is more than one active context object for the given scope[name=" + scopeType.getName() + "]");
        }

        return ret.iterator().next();
    }

    public void clearContexts() {
        contexts.clear();
    }

    public Bean<?> getBean(final Type beanType, final Set<Annotation> qualifiers) {
        for (final Bean<?> bean : beans) {
            if (Reflections.isConcrete(beanType) && qualifiers.isEmpty()) {
                if (bean.getBeanClass().equals(beanType)) {
                    return bean;
                }
            }

            if (bean.getTypes().contains(beanType)) {
                final Set<Annotation> beanQualifiers = bean.getQualifiers();
                final Annotation named = Beans.selectNamedQualifier(qualifiers);

                if (named == null) {
                    if (beanQualifiers.containsAll(qualifiers)) {
                        return bean;
                    }
                } else {
                    final Annotation beanNamed = Beans.selectNamedQualifier(beanQualifiers);

                    if (qualifiers.size() == 1) {
                        if (beanNamed.equals(named)) {
                            return bean;

                        }
                    } else {
                        if (beanQualifiers.equals(qualifiers)) {
                            return bean;
                        }
                    }
                }
            }
        }

        throw new RuntimeException("Not found bean[beanType=" + beanType + "]");
    }

    public Bean<?> getBean(final String name) {
        final Set<Bean<?>> ret = getBeans(name);

        if (null == ret) {
            return null;
        }

        if (0 == ret.size()) {
            return null;
        }

        if (1 == ret.size()) {
            return (Bean<?>) ret.iterator().next();
        }

        throw new RuntimeException("Ambiguous resolution by name!");
    }

    public Object getReference(final Bean<?> bean, final CreationalContext<?> creationalContext) {
        return getReference(bean, null, creationalContext);
    }

    public <T> T getReference(final Bean<T> bean) {
        return (T) getReference(bean, new CreationalContextImpl(bean));
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