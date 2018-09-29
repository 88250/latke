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
package org.b3log.latke.ioc.point;

import org.b3log.latke.ioc.annotated.Annotated;
import org.b3log.latke.ioc.annotated.AnnotatedField;
import org.b3log.latke.ioc.annotated.AnnotatedParameter;
import org.b3log.latke.ioc.bean.Bean;

import java.lang.reflect.Member;

/**
 * Injection Point.
 */
public interface InjectionPoint {

    /**
     * Get the {@link Bean} object representing the
     * bean that defines the injection point. If the injection point does not
     * belong to a bean, return a null value.
     *
     * @return the {@link Bean} object representing
     * bean that defines the injection point, of null if the injection
     * point does not belong to a bean
     */
    Bean<?> getBean();

    /**
     * Get the {@link java.lang.reflect.Field} object in the case of field
     * injection, the {@link java.lang.reflect.Method} object in
     * the case of method parameter injection or the
     * {@link java.lang.reflect.Constructor} object in the case of constructor
     * parameter injection.
     *
     * @return the member
     */
    Member getMember();

    /**
     * Obtain an instance of {@link AnnotatedField} or {@link AnnotatedParameter}, depending upon
     * whether the injection point is an injected field or a constructor/method parameter.
     *
     * @return an {@code AnnotatedField} or {@code AnnotatedParameter}
     */
    Annotated getAnnotated();
}
