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
package org.b3log.latke.ioc.bean;

import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.annotated.*;
import org.b3log.latke.ioc.annotated.AnnotatedType;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.context.CreationalContext;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.point.FieldInjectionPoint;
import org.b3log.latke.ioc.point.InjectionPoint;
import org.b3log.latke.ioc.point.ParameterInjectionPoint;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.util.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

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
     * Constructor parameter injection points.
     */
    private Map<AnnotatedConstructor<T>, List<ParameterInjectionPoint>> constructorParameterInjectionPoints;

    /**
     * Method parameter injection points.
     */
    private Map<AnnotatedMethod<?>, List<ParameterInjectionPoint>> methodParameterInjectionPoints;

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

        constructorParameterInjectionPoints = new HashMap<>();
        methodParameterInjectionPoints = new HashMap<>();
        fieldInjectionPoints = new HashSet<>();

        initFieldInjectionPoints();
        initConstructorInjectionPoints();
        initMethodInjectionPoints();
    }

    /**
     * Resolves dependencies for the specified reference.
     *
     * @param reference the specified reference
     * @throws Exception exception
     */
    private void resolveDependencies(final Object reference) throws Exception {
        final Class<?> superclass = reference.getClass().getSuperclass().getSuperclass(); // Proxy -> Orig -> Super

        resolveSuperclassFieldDependencies(reference, superclass);
        resolveSuperclassMethodDependencies(reference, superclass);
        resolveCurrentclassFieldDependencies(reference);
        resolveCurrentclassMethodDependencies(reference);
    }

    /**
     * Constructs the bean object with dependencies resolved.
     *
     * @return bean object
     * @throws Exception exception
     */
    private T instantiateReference() throws Exception {
        T ret;

        if (constructorParameterInjectionPoints.size() == 1) {
            // only one constructor allow to be annotated with @Inject
            // instantiate an instance by the constructor annotated with @Inject
            final AnnotatedConstructor<T> annotatedConstructor = constructorParameterInjectionPoints.keySet().iterator().next();
            final List<ParameterInjectionPoint> paraInjectionPoints = constructorParameterInjectionPoints.get(annotatedConstructor);
            final Object[] args = new Object[paraInjectionPoints.size()];
            int i = 0;

            for (final ParameterInjectionPoint paraInjectionPoint : paraInjectionPoints) {
                final Object arg = beanManager.getInjectableReference(paraInjectionPoint, null);
                args[i++] = arg;
            }

            final Constructor<T> oriBeanConstructor = annotatedConstructor.getJavaMember();
            final Constructor<T> constructor = proxyClass.getConstructor(oriBeanConstructor.getParameterTypes());

            ret = constructor.newInstance(args);
        } else {
            ret = proxyClass.newInstance();
        }

        ((ProxyObject) ret).setHandler(javassistMethodHandler);
        LOGGER.log(Level.TRACE, "Uses Javassist method handler for bean[class={0}]", beanClass.getName());

        return ret;
    }

    /**
     * Resolves current class field dependencies for the specified reference.
     *
     * @param reference the specified reference
     */
    private void resolveCurrentclassFieldDependencies(final Object reference) {
        for (final FieldInjectionPoint injectionPoint : fieldInjectionPoints) {
            final Object injection = beanManager.getInjectableReference(injectionPoint, null);
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
     * Resolves current class method dependencies for the specified reference.
     *
     * @param reference the specified reference
     */
    private void resolveCurrentclassMethodDependencies(final Object reference) {
        for (final Map.Entry<AnnotatedMethod<?>, List<ParameterInjectionPoint>> methodParameterInjectionPoint
                : methodParameterInjectionPoints.entrySet()) {
            final List<ParameterInjectionPoint> paraSet = methodParameterInjectionPoint.getValue();
            final Object[] args = new Object[paraSet.size()];
            int i = 0;

            for (final ParameterInjectionPoint paraInjectionPoint : paraSet) {
                final Object arg = beanManager.getInjectableReference(paraInjectionPoint, null);
                args[i++] = arg;
            }

            final AnnotatedMethod<?> annotatedMethod = methodParameterInjectionPoint.getKey();
            final Method method = annotatedMethod.getJavaMember();

            try {
                final Method declaredMethod = proxyClass.getDeclaredMethod(method.getName(), method.getParameterTypes());

                try {
                    declaredMethod.setAccessible(true);
                    declaredMethod.invoke(reference, args);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (final NoSuchMethodException ex) {
                try {
                    method.setAccessible(true);
                    method.invoke(reference, args);
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
     * @throws Exception exception
     */
    private void resolveSuperclassFieldDependencies(final Object reference, final Class<?> clazz) throws Exception {
        if (clazz.equals(Object.class)) {
            return;
        }

        final Class<?> superclass = clazz.getSuperclass();

        resolveSuperclassFieldDependencies(reference, superclass);

        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            return;
        }

        final Bean<?> bean = (Bean<?>) beanManager.getBean(clazz);
        final Set<FieldInjectionPoint> injectionPoints = bean.fieldInjectionPoints;

        for (final FieldInjectionPoint injectionPoint : injectionPoints) {
            final Object injection = beanManager.getInjectableReference(injectionPoint, null);
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

    /**
     * Resolves super class method dependencies for the specified reference.
     *
     * @param reference the specified reference
     * @param clazz     the super class of the specified reference
     * @throws Exception exception
     */
    private void resolveSuperclassMethodDependencies(final Object reference, final Class<?> clazz) throws Exception {
        if (clazz.equals(Object.class)) {
            return;
        }

        final Class<?> superclass = clazz.getSuperclass();

        resolveSuperclassMethodDependencies(reference, superclass);

        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            return;
        }

        final Bean<?> superBean = beanManager.getBean(clazz);

        for (final Map.Entry<AnnotatedMethod<?>, List<ParameterInjectionPoint>> methodParameterInjectionPoint
                : superBean.methodParameterInjectionPoints.entrySet()) {
            final List<ParameterInjectionPoint> paraSet = methodParameterInjectionPoint.getValue();
            final Object[] args = new Object[paraSet.size()];
            int i = 0;

            for (final ParameterInjectionPoint paraInjectionPoint : paraSet) {
                final Object arg = beanManager.getInjectableReference(paraInjectionPoint, null);
                args[i++] = arg;
            }

            final AnnotatedMethod<?> superAnnotatedMethod = methodParameterInjectionPoint.getKey();

            final Method superMethod = superAnnotatedMethod.getJavaMember();
            final Method overrideMethod = Reflections.getOverrideMethod(superMethod, proxyClass);

            if (superMethod.equals(overrideMethod)) {
                try {
                    superMethod.invoke(reference, args);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }

                return;
            }
        }
    }

    public Set<Class<? extends Annotation>> getStereotypes() {
        return stereotypes;
    }

    public boolean isAlternative() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isNullable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroy(final T instance, final CreationalContext<T> creationalContext) {
        LOGGER.log(Level.DEBUG, "Destroy bean [name={0}]", name);
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        final Set<InjectionPoint> ret = new HashSet<InjectionPoint>();

        for (final List<ParameterInjectionPoint> constructorParameterInjectionPointList : constructorParameterInjectionPoints.values()) {
            ret.addAll(constructorParameterInjectionPointList);
        }

        ret.addAll(fieldInjectionPoints);

        for (final List<ParameterInjectionPoint> methodParameterInjectionPointList : methodParameterInjectionPoints.values()) {
            ret.addAll(methodParameterInjectionPointList);
        }

        return ret;
    }

    public String getName() {
        return name;
    }

    public Set<Type> getTypes() {
        return types;
    }

    public T create(final CreationalContext<T> creationalContext) {
        T ret = null;

        try {
            ret = instantiateReference();

            resolveDependencies(ret);
        } catch (final Exception ex) {
            LOGGER.log(Level.ERROR, ex.getMessage(), ex);
        }

        return ret;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", class=" + beanClass.getName() + ", types=" + types + "]";
    }

    /**
     * Initializes constructor injection points.
     */
    private void initConstructorInjectionPoints() {
        final Set<AnnotatedConstructor<T>> annotatedConstructors = annotatedType.getConstructors();

        for (final AnnotatedConstructor annotatedConstructor : annotatedConstructors) {
            final List<AnnotatedParameter<?>> parameters = annotatedConstructor.getParameters();
            final List<ParameterInjectionPoint> paraInjectionPointArrayList = new ArrayList<>();

            for (final AnnotatedParameter<?> annotatedParameter : parameters) {
                final ParameterInjectionPoint parameterInjectionPoint = new ParameterInjectionPoint(this, annotatedParameter);

                paraInjectionPointArrayList.add(parameterInjectionPoint);
            }

            constructorParameterInjectionPoints.put(annotatedConstructor, paraInjectionPointArrayList);
        }
    }

    /**
     * Initializes method injection points.
     */
    @SuppressWarnings("unchecked")
    private void initMethodInjectionPoints() {
        final Set<AnnotatedMethod<? super T>> annotatedMethods = annotatedType.getMethods();

        for (final AnnotatedMethod annotatedMethod : annotatedMethods) {
            final List<AnnotatedParameter<?>> parameters = annotatedMethod.getParameters();
            final List<ParameterInjectionPoint> paraInjectionPointArrayList = new ArrayList<>();

            for (final AnnotatedParameter<?> annotatedParameter : parameters) {
                final ParameterInjectionPoint parameterInjectionPoint = new ParameterInjectionPoint(this, annotatedParameter);

                paraInjectionPointArrayList.add(parameterInjectionPoint);
            }

            methodParameterInjectionPoints.put(annotatedMethod, paraInjectionPointArrayList);
        }
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
