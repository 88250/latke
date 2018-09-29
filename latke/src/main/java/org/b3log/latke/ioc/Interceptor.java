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
package org.b3log.latke.ioc;

import org.b3log.latke.intercept.annotation.AfterMethod;
import org.b3log.latke.intercept.annotation.BeforeMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * An interceptor is a Java method that annotated with {@link BeforeMethod} or {@link AfterMethod}.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
 */
public final class Interceptor {

    /**
     * Intercept annotation.
     *
     * <p>
     * For example, {@link BeforeMethod}.
     * </p>
     */
    private Annotation interceptAnnotation;

    /**
     * The name of original method be invoking in application code.
     *
     * <p>
     * For example, {@code org.b3log.A#oneMethod}.
     * </p>
     */
    private String invokingMethodName;

    /**
     * Intercept method.
     */
    private Method interceptMethod;

    /**
     * Constructs an interceptor with the specified intercept method and intercept annotation.
     *
     * @param interceptMethod     the specified intercept method
     * @param interceptAnnotation the specified intercept annotation
     */
    public Interceptor(final Method interceptMethod, final Annotation interceptAnnotation) {
        this.interceptAnnotation = interceptAnnotation;

        if (BeforeMethod.class.equals(interceptAnnotation.annotationType())) {
            this.invokingMethodName = ((BeforeMethod) interceptAnnotation).value();
        } else if (AfterMethod.class.equals(interceptAnnotation.annotationType())) {
            this.invokingMethodName = ((AfterMethod) interceptAnnotation).value();
        }

        if (-1 == invokingMethodName.indexOf("#")) {
            invokingMethodName = interceptMethod.getDeclaringClass().getName() + '#' + invokingMethodName;
        }

        this.interceptMethod = interceptMethod;
    }

    /**
     * Gets intercept method.
     *
     * @return intercept method
     */
    public Method getInterceptMethod() {
        return interceptMethod;
    }

    /**
     * Gets invoking method name.
     *
     * @return invoking method name
     */
    public String getInvokingMethodName() {
        return invokingMethodName;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("invokingMethodName=").append(invokingMethodName).append(", interceptMethod=").append(interceptMethod.toString());

        return builder.toString();
    }
}
