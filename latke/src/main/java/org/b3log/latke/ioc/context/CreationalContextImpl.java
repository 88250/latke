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
        this.incompleteInstances = new HashMap<>();
        this.bean = bean;
        this.outer = true;
    }

    @Override
    public void push(final T incompleteInstance) {
        incompleteInstances.put(bean, incompleteInstance);
    }

    @Override
    public void release() {
    }
}
