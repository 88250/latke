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

import java.util.HashMap;
import java.util.Map;

/**
 * Creational context.
 *
 * @param <T> the type of incomplete instance
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jun 8, 2009
 */
public class CreationalContextImpl<T> implements CreationalContext<T> {

    /**
     * Incomplete instances.
     */
    private final Map<Bean<?>, Object> incompleteInstances;

    /**
     * Bean.
     */
    private final Bean<T> bean;

    /**
     * Whether the bean is outer or not.
     */
    private final boolean outer;

    /**
     * Constructs a creational context with the specified bean.
     * 
     * @param bean the specified bean
     */
    public CreationalContextImpl(final Bean<T> bean) {
        this.incompleteInstances = new HashMap<Bean<?>, Object>();
        this.bean = bean;
        this.outer = true;
    }

    /**
     * Constructs a creational context with the specified bean and incomplete instances.
     * 
     * @param bean the specified bean
     * @param incompleteInstances the specified incomplete instances
     */
    private CreationalContextImpl(final Bean<T> bean, final Map<Bean<?>, Object> incompleteInstances) {
        this.incompleteInstances = incompleteInstances;
        this.bean = bean;
        this.outer = false;
    }

    @Override
    public void push(final T incompleteInstance) {
        incompleteInstances.put(bean, incompleteInstance);
    }

    /**
     * Gets a creational context with the specified bean.
     * 
     * @param <S> the type of incomplete bean
     * @param bean the specified bean
     * @return creational context
     */
    public <S> CreationalContextImpl<S> getCreationalContext(final Bean<S> bean) {
        return new CreationalContextImpl<S>(bean, new HashMap<Bean<?>, Object>(incompleteInstances));
    }

    /**
     * Gets incomplete instance of the specified bean.
     * 
     * @param <S> the type of incomplete bean
     * @param bean the specified bean
     * @return incomplete instance
     */
    public <S> S getIncompleteInstance(final Bean<S> bean) {
        return (S) incompleteInstances.get(bean);
    }

    /**
     * Determines whether contains the incomplete instance of the specified bean.
     * 
     * @param bean the specified bean
     * @return {@code true} if contains, returns {@code false} otherwise
     */
    public boolean containsIncompleteInstance(final Bean<?> bean) {
        return incompleteInstances.containsKey(bean);
    }

    /**
     * Determines whether the bean is outer.
     * 
     * @return {@code true} if it is outer, returns {@code false} otherwise
     */
    public boolean isOuter() {
        return outer;
    }

    @Override
    public void release() {// TODO: CreationalContextImpl.release();
    }
}
