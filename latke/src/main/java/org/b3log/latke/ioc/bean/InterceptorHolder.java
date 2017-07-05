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
package org.b3log.latke.ioc.bean;


import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.b3log.latke.intercept.annotation.AfterMethod;
import org.b3log.latke.intercept.annotation.BeforeMethod;


/**
 * Interceptor holder.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Sep 29, 2013
 */
public final class InterceptorHolder {

    /**
     * &lt;invokingMethodName, interceptors&gt;.
     */
    private static final Map<String, Set<Interceptor>> BEFORE_METHOD_HOLDER = new HashMap<String, Set<Interceptor>>();

    /**
     * &lt;invokingMethodName, interceptors&gt;.
     */
    private static final Map<String, Set<Interceptor>> AFTER_METHOD_HOLDER = new HashMap<String, Set<Interceptor>>();

    /**
     * Private constructor.
     */
    private InterceptorHolder() {}

    /**
     * Adds the specified interceptor with the specified intercept annotation class.
     * 
     * @param interceptor the specified interceptor
     * @param interceptAnnClass the specified intercept annotation class
     */
    public static void addInterceptor(final Interceptor interceptor, final Class<? extends Annotation> interceptAnnClass) {
        if (BeforeMethod.class.equals(interceptAnnClass)) {
            final String invokingMethodName = interceptor.getInvokingMethodName();

            Set<Interceptor> interceptors = BEFORE_METHOD_HOLDER.get(invokingMethodName);

            if (null == interceptors) {
                interceptors = new HashSet<Interceptor>();
                BEFORE_METHOD_HOLDER.put(invokingMethodName, interceptors);
            }

            interceptors.add(interceptor);
        } else if (AfterMethod.class.equals(interceptAnnClass)) {
            final String invokingMethodName = interceptor.getInvokingMethodName();

            Set<Interceptor> interceptors = AFTER_METHOD_HOLDER.get(invokingMethodName);

            if (null == interceptors) {
                interceptors = new HashSet<Interceptor>();
                AFTER_METHOD_HOLDER.put(invokingMethodName, interceptors);
            }

            interceptors.add(interceptor);
        }
    }

    /**
     * Gets interceptors specified with the given invoking method name and intercept annotation class.
     * 
     * @param invokingMethodName the given invoking method name
     * @param interceptAnnClass the specified intercept annotation class
     * @return interceptors, returns an empty list if not found
     */
    static Set<Interceptor> getInterceptors(final String invokingMethodName, final Class<? extends Annotation> interceptAnnClass) {
        if (BeforeMethod.class.equals(interceptAnnClass)) {
            final Set<Interceptor> ret = BEFORE_METHOD_HOLDER.get(invokingMethodName);

            if (null == ret) {
                return Collections.emptySet();
            }

            return ret;
        } else if (AfterMethod.class.equals(interceptAnnClass)) {
            final Set<Interceptor> ret = AFTER_METHOD_HOLDER.get(invokingMethodName);

            if (null == ret) {
                return Collections.emptySet();
            }

            return ret;
        }

        return Collections.emptySet();
    }
}
