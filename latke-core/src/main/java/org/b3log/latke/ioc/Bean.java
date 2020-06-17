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

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.ioc.annotated.AnnotatedField;
import org.b3log.latke.ioc.annotated.AnnotatedType;
import org.b3log.latke.ioc.annotated.AnnotatedTypeImpl;
import org.b3log.latke.util.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Latke bean implementation.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.9, Sep 29, 2018
 * @since 2.4.18
 */
public class Bean<T> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Bean.class);

    /**
     * Stereo types.
     */
    private final Set<Class<? extends Annotation>> stereotypes;

    /**
     * Bean manager.
     */
    private final BeanManager beanManager;

    /**
     * Bean configurator.
     */
    private final Configurator configurator;

    /**
     * Bean name.
     */
    private final String name;

    /**
     * Bean class.
     */
    private final Class<T> beanClass;

    /**
     * Proxy class.
     */
    private final Class<T> proxyClass;

    /**
     * Javassist method handler.
     */
    private final JavassistMethodHandler javassistMethodHandler;

    /**
     * Bean types.
     */
    private final Set<Type> types;

    /**
     * Annotated type of this bean.
     */
    private final AnnotatedType<T> annotatedType;

    /**
     * Field injection points.
     */
    private final Set<FieldInjectionPoint> fieldInjectionPoints;

    /**
     * Constructs a Latke bean.
     *
     * @param beanManager the specified bean manager
     * @param name        the specified bean name
     * @param beanClass   the specified bean class
     * @param types       the specified bean types
     * @param stereotypes the specified stereo types
     */
    public Bean(final BeanManager beanManager, final String name, final Class<T> beanClass, final Set<Type> types,
                final Set<Class<? extends Annotation>> stereotypes) {
        this.beanManager = beanManager;
        this.name = name;
        this.beanClass = beanClass;
        this.types = types;
        this.stereotypes = stereotypes;

        this.configurator = beanManager.getConfigurator();

        javassistMethodHandler = new JavassistMethodHandler(beanManager);
        final ProxyFactory proxyFactory = new ProxyFactory();

        proxyFactory.setSuperclass(beanClass);
        proxyFactory.setFilter(javassistMethodHandler.getMethodFilter());
        proxyClass = (Class<T>) proxyFactory.createClass();

        annotatedType = new AnnotatedTypeImpl<>(beanClass);
        fieldInjectionPoints = new HashSet<>();

        initFieldInjectionPoints();
    }

    /**
     * Resolves dependencies for the specified reference.
     *
     * @param reference the specified reference
     */
    private void resolveDependencies(final Object reference) {
        final Class<?> superclass = reference.getClass().getSuperclass().getSuperclass(); // Proxy -> Orig -> Super

        resolveSuperclassFieldDependencies(reference, superclass);
        resolveCurrentclassFieldDependencies(reference);
    }

    /**
     * Constructs the bean object with dependencies resolved.
     *
     * @return bean object
     * @throws Exception exception
     */
    private T instantiateReference() throws Exception {
        final T ret = proxyClass.newInstance();
        ((ProxyObject) ret).setHandler(javassistMethodHandler);

        LOGGER.log(Level.TRACE, "Uses Javassist method handler for bean [class={}]", beanClass.getName());

        return ret;
    }

    /**
     * Resolves current class field dependencies for the specified reference.
     *
     * @param reference the specified reference
     */
    private void resolveCurrentclassFieldDependencies(final Object reference) {
        for (final FieldInjectionPoint injectionPoint : fieldInjectionPoints) {
            final Object injection = beanManager.getInjectableReference(injectionPoint);
            final Field field = injectionPoint.getAnnotated().getJavaMember();

            try {
                final Field declaredField = proxyClass.getDeclaredField(field.getName());

                if (declaredField.isAnnotationPresent(Inject.class)) {
                    try {
                        declaredField.set(reference, injection);
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (final NoSuchFieldException ex) {
                try {
                    field.set(reference, injection);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Resolves super class field dependencies for the specified reference.
     *
     * @param reference the specified reference
     * @param clazz     the super class of the specified reference
     */
    private void resolveSuperclassFieldDependencies(final Object reference, final Class<?> clazz) {
        if (clazz.equals(Object.class)) {
            return;
        }

        final Class<?> superclass = clazz.getSuperclass();

        resolveSuperclassFieldDependencies(reference, superclass);

        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            return;
        }

        final Bean<?> bean = beanManager.getBean(clazz);
        final Set<FieldInjectionPoint> injectionPoints = bean.fieldInjectionPoints;

        for (final FieldInjectionPoint injectionPoint : injectionPoints) {
            final Object injection = beanManager.getInjectableReference(injectionPoint);
            final Field field = injectionPoint.getAnnotated().getJavaMember();

            try {
                final Field declaredField = proxyClass.getDeclaredField(field.getName());

                if (!Reflections.matchInheritance(declaredField, field)) { // Hide
                    try {
                        field.set(reference, injection);
                    } catch (final Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (final NoSuchFieldException ex) {
                throw new RuntimeException(ex);

            }
        }
    }

    public Set<Class<? extends Annotation>> getStereotypes() {
        return stereotypes;
    }

    public void destroy(final T instance) {
        LOGGER.log(Level.DEBUG, "Destroyed bean [name={}]", name);
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getName() {
        return name;
    }

    public Set<Type> getTypes() {
        return types;
    }

    public T create() {
        T ret = null;
        try {
            ret = instantiateReference();
            resolveDependencies(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Creates bean [name=" + name + "] failed", e);
        }

        return ret;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", class=" + beanClass.getName() + ", types=" + types + "]";
    }

    /**
     * Initializes field injection points.
     */
    private void initFieldInjectionPoints() {
        final Set<AnnotatedField<? super T>> annotatedFields = annotatedType.getFields();

        for (final AnnotatedField<? super T> annotatedField : annotatedFields) {
            final FieldInjectionPoint fieldInjectionPoint = new FieldInjectionPoint(this, annotatedField);

            fieldInjectionPoints.add(fieldInjectionPoint);
        }
    }
}
