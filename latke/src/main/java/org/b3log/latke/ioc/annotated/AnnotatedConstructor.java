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
package org.b3log.latke.ioc.annotated;

import java.lang.reflect.Constructor;

/**
 * <p>Represents a constructor of a Java class.</p>
 *
 * @param <X> the declaring class
 * @author Gavin King
 * @author Pete Muir
 * @see Constructor
 */
public interface AnnotatedConstructor<X> extends AnnotatedCallable<X> {

    /**
     * <p>Get the underlying {@link Constructor}.</p>
     *
     * @return the constructor
     */
    public Constructor<X> getJavaMember();

}
