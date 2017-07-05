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


import org.b3log.latke.ioc.context.AbstractContext;
import org.b3log.latke.ioc.inject.Singleton;

import java.lang.annotation.Annotation;


/**
 * Singleton context.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Nov 27, 2009
 */
public final class SingletonContext extends AbstractContext {

    /**
     * Constructs an singleton context.
     */
    public SingletonContext() {
        this(Singleton.class);
    }

    /**
     * Constructs an singleton context with the specified scope type.
     *
     * @param scopeType the specified scope type
     */
    private SingletonContext(final Class<? extends Annotation> scopeType) {
        super(scopeType);
    }
}
