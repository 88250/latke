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
package org.b3log.latke.ioc.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.annotated.Annotated;
import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.inject.Provider;
import org.b3log.latke.ioc.inject.Singleton;
import org.b3log.latke.ioc.util.Beans;
import org.b3log.latke.util.Reflections;

/**
 * Abstract provider.
 *
 * @param <T> the instance type to provide
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Nov 24, 2009
 */
public abstract class AbstractProvider<T> implements Provider<T> {

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    /**
     * Bean configurator.
     */
    private  Configurator configurator;

    /**
     * Annotated element.
     */
    private Annotated annotated;

    /**
     * Required type.
     */
    private Type requiredType;

    /**
     * Constructs a provider with the specified annotated element and bean manager.
     * 
     * @param beanManager the specified bean manager
     * @param annotated the specified annotated element
     */
    public AbstractProvider(final LatkeBeanManager beanManager, final Annotated annotated) {
        this.beanManager = beanManager;
        this.annotated = annotated;

        configurator = beanManager.getConfigurator();
        requiredType = ((ParameterizedType) annotated.getBaseType()).getActualTypeArguments()[0];
    }

    /**
     * Gets annotated element.
     * 
     * @return annotated element
     */
    public Annotated getAnnotated() {
        return annotated;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        final Set<Annotation> annotations = annotated.getAnnotations();
        Set<Annotation> requiredQualifiers = Beans.selectQualifiers(annotations);
        Class<?> beanClass;
        if (null == requiredQualifiers) {
            // the requiredType is a concrete class or an abstract/interface
            // that has only one implementation absolutely, because the
            // requiredQualifiers equals to null and this injection point
            // passed the configurator validation.
            if (!Reflections.isConcrete(requiredType)) {
                final Set<Class<?>> bindedBeanClasses = configurator.getBindedBeanClasses(requiredType);
                assert bindedBeanClasses.size() == 1;
                beanClass = bindedBeanClasses.iterator().next();
            } else {
                beanClass = (Class<?>) requiredType;
            }

            requiredQualifiers = configurator.getBindedQualifiers(beanClass);
        } else {
            final Set<Class<?>> bindedBeanClasses = configurator.getBindedBeanClasses(requiredType);
            for (final Class<?> bindedBeanClass : bindedBeanClasses) {
                final Set<Annotation> bindedQualifiers = configurator.getBindedQualifiers(bindedBeanClass);
                if (bindedQualifiers.containsAll(requiredQualifiers)) {
                    beanClass = bindedBeanClass;
                    requiredQualifiers = bindedQualifiers;

                    break;
                }
            }
        }

        final Bean<?> bean = beanManager.getBean(requiredType, requiredQualifiers);
        if (bean.getScope() != Singleton.class) {
            return (T) bean.create(null);
        } else {
            return (T) beanManager.getReference(bean, requiredType, null);
        }
    }
}
