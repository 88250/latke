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

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.b3log.latke.ioc.annotated.AnnotatedField;
import org.b3log.latke.ioc.annotated.AnnotatedType;
import org.b3log.latke.ioc.annotated.AnnotatedTypeImpl;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
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
    private static final Logger LOGGER = Logger.getLogger(Bean.class);

    /**
     * Stereo types.
     */
    private final Set<Class<? extends Annotation>> stereotypes;

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    /**
     * Bean configurator.
     */
    private Configurator configurator;

    /**
     * Bean name.
     */
    private String name;

    /**
     * Bean class.
     */
    private Class<T> beanClass;

    /**
     * Proxy class.
     */
    private Class<T> proxyClass;

    /**
     * Javassist method handler.
     */
    private JavassistMethodHandler javassistMethodHandler;

    /**
     * Bean types.
     */
    private Set<Type> types;

    /**
     * Annotated type of this bean.
     */
    private AnnotatedType<T> annotatedType;

    /**
     * Field injection points.
     */
    private Set<FieldInjectionPoint> fieldInjectionPoints;

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

        annotatedType = new AnnotatedTypeImpl<T>(beanClass);
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

        LOGGER.log(Level.TRACE, "Uses Javassist method handler for bean [class={0}]", beanClass.getName());

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
        LOGGER.log(Level.DEBUG, "Destroyed bean [name={0}]", name);
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
