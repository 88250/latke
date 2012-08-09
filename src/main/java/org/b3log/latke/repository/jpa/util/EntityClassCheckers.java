/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.repository.jpa.util;

/**
 * Entity class check utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 14, 2011
 */
public final class EntityClassCheckers {

    /**
     * Checks whether the specified class is a valid entity class.
     * 
     * <p>
     * A valid entity class:
     * <ul>
     *   <li>Annotated with {@link javax.persistence.Entity}</li>
     *   <li>Has a member field annotated with {@link javax.persistence.Id} and 
     *   its type is {@link String}</li>
     *   <li>Types of other member fields must be {@link Class#isPrimitive()
     *   primitive}, {@link String} or {@link java.util.Collection}.</li>
     * </ul>
     * </p>
     * 
     * @param clazz the specified class
     * @return {@code true} if it is valid, returns {@code false} otherwise 
     */
    public static boolean isValid(final Class<?> clazz) {
        // TODO: zezhou jiang, checks whether the specified entity class is valid.
        
        return true;
    }

    /**
     * Private constructor.
     */
    private EntityClassCheckers() {
    }
}
