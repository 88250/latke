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


import org.b3log.latke.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A callable java type.
 *
 * @param <T> the declaring type
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Mar 30, 2010
 */
public abstract class AbstractAnnotatedCallableImpl<T> implements AnnotatedCallable<T> {

    /**
     * Member.
     */
    private Member member;

    /**
     * Parameters of a constructor or a method (for the member).
     */
    private List<AnnotatedParameter<T>> parameters;

    /**
     * Constructs a annotated callable java type with the specified member.
     * 
     * @param member the specified member
     */
    public AbstractAnnotatedCallableImpl(final Member member) {
        this.member = member;
        parameters = new ArrayList<AnnotatedParameter<T>>();

        Type[] parameterTypes = null;
        Annotation[][] parameterAnnotations = null;

        if (member instanceof Method) {
            parameterTypes = ((Method) member).getGenericParameterTypes();
            parameterAnnotations = ((Method) member).getParameterAnnotations();
        } else if (member instanceof Constructor) {
            parameterTypes = ((Constructor) member).getGenericParameterTypes();
            parameterAnnotations = ((Constructor) member).getParameterAnnotations();
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            final Type parameter = parameterTypes[i];

            // XXX: consider all annotations as the inject relevent annotation
            final Annotation[] annotations = parameterAnnotations[i];
            final Set<Annotation> annotationSet = CollectionUtils.arrayToSet(annotations);
            final AnnotatedParameter<T> annotatedParameter = new AnnotatedParameterImpl<T>(this, parameter, i, annotationSet);

            this.parameters.add(annotatedParameter);
        }
    }

    @Override
    public AnnotatedType<T> getDeclaringType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<AnnotatedParameter<T>> getParameters() {
        return parameters;
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(member.getModifiers());
    }
    
    /**
     * Gets the member.
     * 
     * @return member
     */
    public Member getMember() {
        return member;
    }
}
