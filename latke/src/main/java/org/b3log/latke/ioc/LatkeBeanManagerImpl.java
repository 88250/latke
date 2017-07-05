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


import org.b3log.latke.event.EventManager;
import org.b3log.latke.ioc.annotated.AnnotatedType;
import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.config.impl.ConfiguratorImpl;
import org.b3log.latke.ioc.context.*;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Provider;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.ioc.point.InjectionPoint;
import org.b3log.latke.ioc.point.InjectionTarget;
import org.b3log.latke.ioc.util.Beans;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.service.LangPropsServiceImpl;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.util.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Latke bean manager implementation.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Jul 5, 2017
 */
@Named("beanManager")
@Singleton
public class LatkeBeanManagerImpl implements LatkeBeanManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LatkeBeanManagerImpl.class);

    /**
     * Built-in beans.
     */
    private static Set<LatkeBean<?>> builtInBeans;

    /**
     * Built-in bean classes.
     */
    private static List<Class<?>> builtInBeanClasses = Arrays.<Class<?>>asList(
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
    private Set<LatkeBean<?>> beans;

    /**
     * Contexts.
     */
    private Map<Class<? extends Annotation>, Set<Context>> contexts;

    /**
     * Constructs a Latke bean manager.
     */
    private LatkeBeanManagerImpl() {
        LOGGER.log(Level.DEBUG, "Creating Latke bean manager");

        beans = new HashSet<LatkeBean<?>>();
        contexts = new HashMap<Class<? extends Annotation>, Set<Context>>();
        builtInBeans = new HashSet<LatkeBean<?>>();
        configurator = new ConfiguratorImpl(this);

        // Init Singleton context
        final SingletonContext singletonContext = new SingletonContext();
        final Bean<LatkeBeanManagerImpl> beanManagerBean = configurator.createBean(LatkeBeanManagerImpl.class);

        singletonContext.add(beanManagerBean, this);
        singletonContext.setActive(true);
        addContext(singletonContext);

        // Constructs the built-in beans with singleton
        for (final Class<?> builtInBeanClass : builtInBeanClasses) {
            final LatkeBean<?> builtInBean = configurator.createBean(builtInBeanClass);

            builtInBeans.add(builtInBean);
            singletonContext.get(builtInBean, new CreationalContextImpl(builtInBean));
        }

        beans.addAll(builtInBeans);

        LOGGER.log(Level.DEBUG, "Created Latke bean manager");
    }

    /**
     * Gets the root bean manager.
     *
     * @return the root bean manager
     */
    public static LatkeBeanManager getInstance() {
        return BeanManagerHolder.instance;
    }

    @Override
    public void addBean(final LatkeBean<?> bean) {
        beans.add(bean);
    }

    @Override
    public Set<LatkeBean<?>> getBeans() {
        return beans;
    }

    @Override
    public Set<LatkeBean<?>> getBeans(final Class<? extends Annotation> stereoType) {
        final Set<LatkeBean<?>> ret = new HashSet<LatkeBean<?>>();

        for (final LatkeBean<?> bean : beans) {
            final Set<Class<? extends Annotation>> stereotypes = bean.getStereotypes();

            if (stereotypes.contains(stereoType)) {
                ret.add(bean);
            }
        }

        return ret;
    }

    @Override
    public <T> LatkeBean<T> getBean(final Class<T> beanClass) {
        for (final LatkeBean<?> bean : beans) {
            if (bean.getBeanClass().equals(beanClass)) {
                return (LatkeBean<T>) bean;
            }
        }

        throw new RuntimeException("Can not get bean with class [" + beanClass.getName() + ']');
    }

    @Override
    public Configurator getConfigurator() {
        return configurator;
    }

    @Override
    public Object getReference(final Bean bean, final Type beanType, final CreationalContext ctx) {
        final Context activeContext = getContext(bean.getScope());

        return activeContext.get(bean, ctx);
    }

    @Override
    public Object getInjectableReference(final InjectionPoint ij, final CreationalContext<?> ctx) {
        final Type baseType = ij.getAnnotated().getBaseType();

        if (baseType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) baseType;
            final Type type = parameterizedType.getRawType();

            if (type.equals(Provider.class)) {
                return null;
            }
        }

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

    @Override
    public <T> CreationalContext<T> createCreationalContext(final Contextual<T> contextual) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Bean<?>> getBeans(final Type beanType, final Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Bean<?>> getBeans(final String name) {
        final Set<Bean<?>> ret = new HashSet<Bean<?>>();

        for (final Bean<?> bean : beans) {
            if (bean.getName().equals(name)) {
                ret.add(bean);
            }
        }

        return ret;
    }

    @Override
    public Bean<?> getPassivationCapableBean(final String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <X> Bean<? extends X> resolve(final Set<Bean<? extends X>> beans) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate(final InjectionPoint injectionPoint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fireEvent(final Object event, final Annotation... qualifiers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isScope(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNormalScope(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isPassivatingScope(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isQualifier(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isInterceptorBinding(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isStereotype(final Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Annotation> getInterceptorBindingDefinition(final Class<? extends Annotation> bindingType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Annotation> getStereotypeDefinition(final Class<? extends Annotation> stereotype) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addContext(final Context context) {
        final Class<? extends Annotation> scope = context.getScope();
        Set<Context> contextSet = contexts.get(scope);

        if (contextSet == null) {
            contextSet = new HashSet<Context>();
        }
        contextSet.add(context);

        contexts.put(scope, contextSet);
    }

    @Override
    public Context getContext(final Class<? extends Annotation> scopeType) {
        final Set<Context> contextSet = contexts.get(scopeType);
        final Set<Context> ret = new HashSet<Context>();

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
            throw new IllegalArgumentException(
                    "There is more than one active context object for the given scope[name=" + scopeType.getName() + "]");
        }

        return ret.iterator().next();
    }

    @Override
    public void clearContexts() {
        contexts.clear();
    }

    @Override
    public <T> AnnotatedType<T> createAnnotatedType(final Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> InjectionTarget<T> createInjectionTarget(final AnnotatedType<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LatkeBean<?> getBean(final Type beanType, final Set<Annotation> qualifiers) {
        for (final LatkeBean<?> bean : beans) {
            if (Reflections.isConcrete(beanType) && qualifiers.isEmpty()) {
                if (bean.getBeanClass().equals((Class<?>) beanType)) {
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

    @Override
    public LatkeBean<?> getBean(final String name) {
        final Set<Bean<?>> ret = getBeans(name);

        if (null == ret) {
            return null;
        }

        if (0 == ret.size()) {
            return null;
        }

        if (1 == ret.size()) {
            return (LatkeBean<?>) ret.iterator().next();
        }

        throw new RuntimeException("Ambiguous resolution by name!");
    }

    @Override
    public Object getReference(final LatkeBean<?> bean, final CreationalContext<?> creationalContext) {
        return getReference(bean, null, creationalContext);
    }

    @Override
    public <T> T getReference(final LatkeBean<T> bean) {
        return (T) getReference(bean, new CreationalContextImpl(bean));
    }

    @Override
    public <T> T getReference(final Class<T> beanClass) {
        final LatkeBean<T> bean = getBean(beanClass);

        return getReference(bean);
    }

    /**
     * Root bean manager holder.
     */
    private static final class BeanManagerHolder {

        /**
         * Singleton of bean manager.
         */
        private static LatkeBeanManager instance = new LatkeBeanManagerImpl();

        /**
         * Private constructor.
         */
        private BeanManagerHolder() {
        }
    }
}
