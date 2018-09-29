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
package org.b3log.latke.ioc.annotated;

import java.util.List;

/**
 * Represents a callable member of a Java type.
 *
 * @param <X> the declaring type
 */
public interface AnnotatedCallable<X> extends AnnotatedMember<X> {

    /**
     * Get the parameters of the callable member.
     *
     * @return the parameters
     */
    List<AnnotatedParameter<X>> getParameters();
}
