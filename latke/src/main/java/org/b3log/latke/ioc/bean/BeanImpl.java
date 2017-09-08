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
package org.b3log.latke.ioc.bean;


import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.annotated.*;
import org.b3log.latke.ioc.annotated.AnnotatedType;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.context.CreationalContext;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Provider;
import org.b3log.latke.ioc.literal.NamedLiteral;
import org.b3log.latke.ioc.point.FieldInjectionPoint;
import org.b3log.latke.ioc.point.InjectionPoint;
import org.b3log.latke.ioc.point.ParameterInjectionPoint;
import org.b3log.latke.ioc.provider.FieldProvider;
import org.b3log.latke.ioc.provider.ParameterProvider;
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
 * @version 1.0.0.8, Sep 29, 2013
 */
public class BeanImpl<T> implements LatkeBean<T> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BeanImpl.class);
    /**
     * Stereo types.
     */
    private final Set<Class<? extends Annotation>> stereotypes;
    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;
    /**
     * Bean configurator.
     */
    private Configurator configurator;
    /**
     * Bean name.
     */
    private String name;
    /**
     * Bean scope.
     */
    private Class<? extends Annotation> scope;
    /**
     * Bean qualifiers.
     */
    private Set<Annotation> qualifiers;
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
     * Constructor parameter providers.
     */
    private List<ParameterProvider<?>> constructorParameterProviders;
    /**
     * Field provider.
     */
    private Set<FieldProvider<?>> fieldProviders;
    /**
     * Method parameter providers.
     */
    private Map<AnnotatedMethod<?>, List<ParameterProvider<?>>> methodParameterProviders;

    /**
     * Constructs a Latke bean.
     *
     * @param beanManager the specified bean manager
     * @param name        the specified bean name
     * @param scope       the specified bean scope
     * @param qualifiers  the specified bean qualifiers
     * @param beanClass   the specified bean class
     * @param types       the specified bean types
     * @param stereotypes the specified stereo types
     */
    public BeanImpl(final LatkeBeanManager beanManager, final String name, final Class<? extends Annotation> scope,
                    final Set<Annotation> qualifiers, final Class<T> beanClass, final Set<Type> types,
                    final Set<Class<? extends Annotation>> stereotypes) {
        this.beanManager = beanManager;
        this.name = name;
        this.scope = scope;
        this.qualifiers = qualifiers;
        this.beanClass = beanClass;
        this.types = types;
        this.stereotypes = stereotypes;

        this.configurator = beanManager.getConfigurator();

        javassistMethodHandler = new JavassistMethodHandler(beanManager);
        final ProxyFactory proxyFactory = new ProxyFactory();

        proxyFactory.setSuperclass(beanClass);
        proxyFactory.setFilter(javassistMethodHandler.getMethodFilter());
        proxyClass = proxyFactory.createClass();

        annotatedType = new AnnotatedTypeImpl<T>(beanClass);

        constructorParameterInjectionPoints = new HashMap<AnnotatedConstructor<T>, List<ParameterInjectionPoint>>();
        constructorParameterProviders = new ArrayList<ParameterProvider<?>>();
        methodParameterInjectionPoints = new HashMap<AnnotatedMethod<?>, List<ParameterInjectionPoint>>();
        methodParameterProviders = new HashMap<AnnotatedMethod<?>, List<ParameterProvider<?>>>();
        fieldInjectionPoints = new HashSet<FieldInjectionPoint>();
        fieldProviders = new HashSet<FieldProvider<?>>();

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
                Object arg = beanManager.getInjectableReference(paraInjectionPoint, null);

                if (arg == null) {
                    for (final ParameterProvider<?> provider : constructorParameterProviders) {
                        if (provider.getAnnotated().equals(paraInjectionPoint.getAnnotated())) {
                            arg = provider;
                            break;
                        }
                    }
                }

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
            Object injection = beanManager.getInjectableReference(injectionPoint, null);

            if (injection == null) {
                for (final FieldProvider<?> provider : fieldProviders) {
                    if (provider.getAnnotated().equals(injectionPoint.getAnnotated())) {
                        injection = provider;
                        break;
                    }
                }
            }

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
                Object arg = beanManager.getInjectableReference(paraInjectionPoint, null);

                if (arg == null) {
                    for (final ParameterProvider<?> provider : methodParameterProviders.get(methodParameterInjectionPoint.getKey())) {
                        if (provider.getAnnotated().equals(paraInjectionPoint.getAnnotated())) {
                            arg = provider;
                            break;
                        }
                    }
                }

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

        final BeanImpl<?> bean = (BeanImpl<?>) beanManager.getBean(clazz);
        final Set<FieldInjectionPoint> injectionPoints = bean.fieldInjectionPoints;

        for (final FieldInjectionPoint injectionPoint : injectionPoints) {
            Object injection = beanManager.getInjectableReference(injectionPoint, null);

            if (injection == null) {
                for (final FieldProvider<?> provider : bean.fieldProviders) {
                    if (provider.getAnnotated().equals(injectionPoint.getAnnotated())) {
                        injection = provider;
                        break;
                    }
                }
            }

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

        final BeanImpl<?> superBean = (BeanImpl<?>) beanManager.getBean(clazz);

        for (final Map.Entry<AnnotatedMethod<?>, List<ParameterInjectionPoint>> methodParameterInjectionPoint
                : superBean.methodParameterInjectionPoints.entrySet()) {
            final List<ParameterInjectionPoint> paraSet = methodParameterInjectionPoint.getValue();
            final Object[] args = new Object[paraSet.size()];
            int i = 0;

            for (final ParameterInjectionPoint paraInjectionPoint : paraSet) {
                Object arg = beanManager.getInjectableReference(paraInjectionPoint, null);

                if (arg == null) {
                    for (final ParameterProvider<?> provider : superBean.methodParameterProviders.get(methodParameterInjectionPoint.getKey())) {
                        if (provider.getAnnotated().equals(paraInjectionPoint.getAnnotated())) {
                            arg = provider;
                            break;
                        }
                    }
                }

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

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return stereotypes;
    }

    @Override
    public boolean isAlternative() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNullable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroy(final T instance, final CreationalContext<T> creationalContext) {
        LOGGER.log(Level.DEBUG, "Destroy bean [name={0}]", name);
    }

    @Override
    public BeanManager getBeanManager() {
        return beanManager;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    /**
     * Sets the scope with the specified scope.
     *
     * @param scope the specified scope
     */
    private void setScope(final Class<? extends Annotation> scope) {
        this.scope = scope;
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
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
    public LatkeBean<T> named(final String name) {
        final Named namedQualifier = new NamedLiteral(name);

        addQualifier(namedQualifier);
        return this;
    }

    @Override
    public LatkeBean<T> qualified(final Annotation qualifier,
                                  final Annotation... qualifiers) {
        addQualifier(qualifier);
        for (final Annotation q : qualifiers) {
            addQualifier(q);
        }

        return this;
    }

    @Override
    public LatkeBean<T> scoped(final Class<? extends Annotation> scope) {
        this.setScope(scope);
        return this;
    }

    @Override
    public String toString() {
        return "[name=" + name + ", scope=" + scope.getName() + ", qualifiers=" + qualifiers + ", class=" + beanClass.getName() + ", types="
                + types + "]";
    }

    /**
     * Initializes constructor injection points.
     */
    private void initConstructorInjectionPoints() {
        final Set<AnnotatedConstructor<T>> annotatedConstructors = annotatedType.getConstructors();

        for (final AnnotatedConstructor annotatedConstructor : annotatedConstructors) {
            final List<AnnotatedParameter<?>> parameters = annotatedConstructor.getParameters();
            final List<ParameterInjectionPoint> paraInjectionPointArrayList = new ArrayList<ParameterInjectionPoint>();

            for (final AnnotatedParameter<?> annotatedParameter : parameters) {
                Type type = annotatedParameter.getBaseType();

                if (type instanceof ParameterizedType) {
                    type = ((ParameterizedType) type).getRawType();
                }

                if (type.equals(Provider.class)) {
                    final ParameterProvider<T> provider = new ParameterProvider<T>(beanManager, annotatedParameter);

                    constructorParameterProviders.add(provider);
                }

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
            final List<ParameterInjectionPoint> paraInjectionPointArrayList = new ArrayList<ParameterInjectionPoint>();
            final List<ParameterProvider<?>> paraProviders = new ArrayList<ParameterProvider<?>>();

            for (final AnnotatedParameter<?> annotatedParameter : parameters) {
                Type type = annotatedParameter.getBaseType();

                if (type instanceof ParameterizedType) {
                    type = ((ParameterizedType) type).getRawType();
                }

                if (type.equals(Provider.class)) {
                    final ParameterProvider<T> provider = new ParameterProvider<T>(beanManager, annotatedParameter);

                    paraProviders.add(provider);
                }

                final ParameterInjectionPoint parameterInjectionPoint = new ParameterInjectionPoint(this, annotatedParameter);

                paraInjectionPointArrayList.add(parameterInjectionPoint);
            }

            methodParameterProviders.put(annotatedMethod, paraProviders);
            methodParameterInjectionPoints.put(annotatedMethod, paraInjectionPointArrayList);
        }
    }

    /**
     * Initializes field injection points.
     */
    private void initFieldInjectionPoints() {
        final Set<AnnotatedField<? super T>> annotatedFields = annotatedType.getFields();

        for (final AnnotatedField<? super T> annotatedField : annotatedFields) {
            final Field field = annotatedField.getJavaMember();

            if (field.getType().equals(Provider.class)) { // by provider
                final FieldProvider<T> provider = new FieldProvider<T>(beanManager, annotatedField);

                fieldProviders.add(provider);

                final FieldInjectionPoint fieldInjectionPoint = new FieldInjectionPoint(this, annotatedField);

                fieldInjectionPoints.add(fieldInjectionPoint);
            } else { // by qualifier
                final FieldInjectionPoint fieldInjectionPoint = new FieldInjectionPoint(this, annotatedField);

                fieldInjectionPoints.add(fieldInjectionPoint);
            }
        }
    }

    /**
     * Adds a qualifier with the specified qualifier.
     *
     * @param qualifier the specified qualifier
     */
    private void addQualifier(final Annotation qualifier) {
        if (qualifier.getClass().equals(NamedLiteral.class)) {
            final NamedLiteral namedQualifier = (NamedLiteral) getNamedQualifier();
            final NamedLiteral newNamedQualifier = (NamedLiteral) qualifier;

            if (!namedQualifier.value().equals(newNamedQualifier.value())) {
                setNamedQualifier(newNamedQualifier);
            }
        } else {
            qualifiers.add(qualifier);
        }

        configurator.addClassQualifierBinding(beanClass, qualifier);
    }

    /**
     * Gets the named qualifier.
     *
     * @return named aualifier
     */
    private Annotation getNamedQualifier() {
        for (final Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Named.class)) {
                return qualifier;
            }
        }

        throw new RuntimeException("A bean has one qualifier(Named) at least!");
    }

    /**
     * Sets the named qualifier with the specified named qualifier.
     *
     * @param namedQualifier the specified named qualifier
     */
    private void setNamedQualifier(final Annotation namedQualifier) {
        for (final Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Named.class)) {
                qualifiers.remove(qualifier);
                qualifiers.add(namedQualifier);
                name = ((Named) namedQualifier).value();
            }
        }
    }
}
