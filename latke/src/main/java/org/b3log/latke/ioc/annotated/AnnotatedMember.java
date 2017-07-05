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

import java.lang.reflect.Member;

/**
 * <p>Represents a member of a Java type.</p>
 *
 * @param <X> the declaring type
 * @author Gavin King
 * @author Pete Muir
 * @see Member
 */
public interface AnnotatedMember<X> extends Annotated {

    /**
     * <p>Get the underlying {@link Member}.</p>
     *
     * @return the {@link Member}
     */
    public Member getJavaMember();

    /**
     * <p>Determines if the member is static.</p>
     *
     * @return <tt>true</tt> if the member is static
     */
    public boolean isStatic();

    /**
     * <p>Get the {@linkplain AnnotatedType type} which declares this
     * member.</p>
     *
     * @return the type which declares this member
     */
    public AnnotatedType<X> getDeclaringType();
}

