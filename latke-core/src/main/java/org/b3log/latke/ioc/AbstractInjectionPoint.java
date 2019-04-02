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

import org.b3log.latke.ioc.annotated.Annotated;

/**
 * Abstract injection point.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @since 2.4.18
 */
public abstract class AbstractInjectionPoint implements InjectionPoint {

    /**
     * Annotated element.
     */
    private Annotated annotated;

    /**
     * Owner bean.
     */
    private Bean<?> ownerBean;

    /**
     * Constructs a injection point with the specified arguments.
     *
     * @param ownerBean the specified owner bean
     * @param annotated the specified annotated element
     */
    public AbstractInjectionPoint(final Bean<?> ownerBean, final Annotated annotated) {
        this.ownerBean = ownerBean;
        this.annotated = annotated;
    }

    @Override
    public Bean<?> getBean() {
        return ownerBean;
    }

    @Override
    public Annotated getAnnotated() {
        return annotated;
    }
}
