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
package org.b3log.latke.ioc.point;

import org.b3log.latke.ioc.annotated.AnnotatedParameter;
import org.b3log.latke.ioc.bean.Bean;

import java.lang.reflect.Member;

/**
 * Parameter injection point.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Nov 16, 2009
 */
public class ParameterInjectionPoint extends AbstractInjectionPoint {

    /**
     * Constructs a parameter injection point.
     * 
     * @param ownerBean the specified owner bean
     * @param annotatedParameter the specified annotated parameter
     */
    public ParameterInjectionPoint(final Bean<?> ownerBean, final AnnotatedParameter<?> annotatedParameter) {
        super(ownerBean, annotatedParameter);
    }

    @Override
    public Member getMember() {
        return getAnnotated().getDeclaringCallable().getJavaMember();
    }

    @Override
    public AnnotatedParameter<?> getAnnotated() {
        return (AnnotatedParameter<?>) super.getAnnotated();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", OwnerBean[name=" + getBean().getName() + "], AnnotatedCallable[" + getMember()
            + "], AnnotatedParameter[" + getAnnotated() + "]";
    }
}
