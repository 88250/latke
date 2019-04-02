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

import org.b3log.latke.ioc.annotated.Annotated;
import org.b3log.latke.ioc.annotated.AnnotatedField;

/**
 * Injection Point.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
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
     * Obtain an instance of {@link AnnotatedField}.
     *
     * @return an {@code AnnotatedField}
     */
    Annotated getAnnotated();
}
