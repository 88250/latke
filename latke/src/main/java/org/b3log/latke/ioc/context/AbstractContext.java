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
package org.b3log.latke.ioc.context;

import org.b3log.latke.ioc.bean.Bean;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract context.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.8, Sep 5, 2013
 */
public abstract class AbstractContext implements LatkeBeansContext {

    /**
     * Whether the context is active or not.
     */
    private boolean active;

    /**
     * Scope type of this context.
     */
    private Class<? extends Annotation> scopeType;

    /**
     * Bean reference in this context.
     */
    private Map<Contextual<?>, Object> beanReferences;

    /**
     * Constructs a context with the specified scope type.
     *
     * @param scopeType the specified scope type
     */
    public AbstractContext(final Class<? extends Annotation> scopeType) {
        this.scopeType = scopeType;

        beanReferences = new HashMap<Contextual<?>, Object>();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public <T> void add(final Contextual<T> bean, final T reference) {
        beanReferences.put(bean, reference);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(final Contextual<T> bean) {
        return getReference(bean, null);
    }

    @Override
    public <T> T get(final Contextual<T> bean, final CreationalContext<T> creationalContext) {
        return getReference(bean, creationalContext);
    }

    /**
     * Gets reference of the specified bean and creational context.
     *
     * @param <T>               the type of contextual
     * @param bean              the specified bean
     * @param creationalContext the specified creational context
     * @return reference
     */
    private <T> T getReference(final Contextual<T> bean, final CreationalContext<T> creationalContext) {
        T ret = (T) beanReferences.get(bean);

        if (null != ret) {
            return ret;
        }

        ret = bean.create(creationalContext);

        if (null != ret) {
            beanReferences.put(bean, ret);

            return ret;
        }

        throw new RuntimeException("Can't create reference for bean[" + bean + "]");
    }

    /**
     * Removes the specified bean.
     *
     * @param <T>  the type of contextual
     * @param bean the specified bean
     */
    public <T> void remove(final Contextual<T> bean) {
        if (null != beanReferences.get(bean)) {
            beanReferences.remove(bean);
        }
    }

    /**
     * Destroys this context, clears all bean's references (instances).
     *
     * @param <T> the type of contextual
     */
    @SuppressWarnings("unchecked")
    public <T> void destroy() {
        final Set<Entry<Contextual<?>, Object>> beanSet = beanReferences.entrySet();
        final Iterator<Entry<Contextual<?>, Object>> i = beanSet.iterator();

        Contextual<?> bean = null;

        while (i.hasNext()) {
            bean = i.next().getKey();
            final T instance = (T) beanReferences.get(bean);

            destroyReference((Bean<T>) bean, instance);
        }

        beanReferences.clear();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scopeType;
    }

    /**
     * Destroys the specified bean's instance.
     *
     * @param <T>          the type of contextual
     * @param bean         the specified bean
     * @param beanInstance the specified bean's instance
     */
    private <T> void destroyReference(final Bean<T> bean, final T beanInstance) {
        // bean.destroy(beanInstance);
        // TODO: bean.destroy(beanInstance, null);
        bean.destroy(beanInstance, null);
    }
}
