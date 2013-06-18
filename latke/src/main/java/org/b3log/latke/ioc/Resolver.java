/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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


import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;


/**
 * Dependency resolver.
 * 
 * <p>
 * Dependency of a bean will be resolved while {@link LatkeBean#create(javax.enterprise.context.spi.CreationalContext) bean creation}.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Mar 30, 2010
 */
public interface Resolver {
    
    /**
     * Resolves dependencies of the specified method.
     * 
     * @param annotatedMethod the specified method
     * @param reference the specified instance of the specified method
     * @param args the prepared arguments
     */
    void resolveMethod(final AnnotatedMethod<?> annotatedMethod, final Object reference, final Object[] args);

    /**
     * Resolves dependency of the specified field.
     * 
     * @param annotatedField the specified field
     * @param reference the specified instance of the specified field
     * @param injection the prepared field value
     */
    void resolveField(final AnnotatedField<?> annotatedField, final Object reference, final Object injection);
}
