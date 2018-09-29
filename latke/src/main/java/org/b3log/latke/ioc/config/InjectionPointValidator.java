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
package org.b3log.latke.ioc.config;

import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.inject.AmbiguousResolutionException;
import org.b3log.latke.ioc.inject.UnsatisfiedResolutionException;
import org.b3log.latke.ioc.point.InjectionPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Injection point validator.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Apr 2, 2010
 */
public final class InjectionPointValidator {

    /**
     * Private constructor.
     */
    private InjectionPointValidator() {
    }

    /**
     * Checks the validity of the specified bean.
     *
     * @param <T>  the class of the bean instance
     * @param bean the specified bean
     */
    public static <T> void checkValidity(final Bean<T> bean) {
        final Set<InjectionPoint> injectionPoints = bean.getInjectionPoints();
        final Set<Constructor> constructors = new HashSet<>();

        for (final InjectionPoint injectionPoint : injectionPoints) {
            final Member member = injectionPoint.getMember();

            if (member instanceof Constructor) {
                constructors.add((Constructor) member);
            }
        }

        if (constructors.size() > 1) {
            throw new RuntimeException("Only one constructor can be injected!");
        }

        // TODO: TypeVarible check.
    }

    /**
     * Checks dependency resolution of the specified bean.
     *
     * @param <T>          the class of the bean instance
     * @param bean         the specified bean
     * @param configurator the specified configurator
     */
    public static <T> void checkDependency(final Bean<T> bean, final Configurator configurator) {
        for (final InjectionPoint injectionPoint : bean.getInjectionPoints()) {
            final Type requiredType = injectionPoint.getType();
            final Set<Class<?>> bindedBeanClasses = configurator.getBindedBeanClasses(requiredType);

            if (bindedBeanClasses == null) {
                throw new UnsatisfiedResolutionException("Has no eligible bean [type=" + requiredType.toString()
                        + "] for injection point [" + injectionPoint + "]");
            } else if (bindedBeanClasses.size() > 1) {
                throw new AmbiguousResolutionException("Has more than one eligible bean[type=" + requiredType.toString()
                        + "] for injection point[" + injectionPoint + "]");
            }
        }
    }
}
