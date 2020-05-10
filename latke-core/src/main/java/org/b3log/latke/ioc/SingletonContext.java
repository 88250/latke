/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
    private final Map<Bean<?>, Object> beanReferences;

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
