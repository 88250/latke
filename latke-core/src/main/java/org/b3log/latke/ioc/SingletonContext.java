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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract context.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.9, Sep 29, 2018
 * @since 2.4.18
 */
public final class SingletonContext {

    /**
     * Bean reference in this context.
     */
    private Map<Bean<?>, Object> beanReferences;

    /**
     * Constructs a context.
     */
    public SingletonContext() {
        beanReferences = new HashMap<>();
    }

    public <T> void add(final Bean<T> bean, final T reference) {
        beanReferences.put(bean, reference);
    }

    public <T> T get(final Bean<T> bean) {
        return getReference(bean);
    }

    /**
     * Gets reference of the specified bean and creational context.
     *
     * @param <T>  the type of contextual
     * @param bean the specified bean
     * @return reference
     */
    private <T> T getReference(final Bean<T> bean) {
        T ret = (T) beanReferences.get(bean);

        if (null != ret) {
            return ret;
        }

        ret = bean.create();

        if (null != ret) {
            beanReferences.put(bean, ret);

            return ret;
        }

        throw new RuntimeException("Can't create reference for bean [" + bean + "]");
    }

    /**
     * Destroys this context, clears all bean's references (instances).
     *
     * @param <T> the type of contextual
     */
    public <T> void destroy() {
        final Set<Entry<Bean<?>, Object>> beanSet = beanReferences.entrySet();
        final Iterator<Entry<Bean<?>, Object>> i = beanSet.iterator();
        Bean<?> bean;
        while (i.hasNext()) {
            bean = i.next().getKey();
            final T instance = (T) beanReferences.get(bean);

            destroyReference((Bean<T>) bean, instance);
        }

        beanReferences.clear();
    }

    /**
     * Destroys the specified bean's instance.
     *
     * @param <T>          the type of contextual
     * @param bean         the specified bean
     * @param beanInstance the specified bean's instance
     */
    private <T> void destroyReference(final Bean<T> bean, final T beanInstance) {
        bean.destroy(beanInstance);
    }
}
