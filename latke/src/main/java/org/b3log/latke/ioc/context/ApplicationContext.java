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


import java.lang.annotation.Annotation;
import org.b3log.latke.ioc.context.AbstractContext;
import org.b3log.latke.ioc.context.ApplicationScoped;


/**
 * Application context.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jun 10, 2009
 */
public final class ApplicationContext extends AbstractContext {

    /**
     * Constructs an application context.
     */
    public ApplicationContext() {
        this(ApplicationScoped.class);
    }

    /**
     * Constructs an application context with the specified scope type.
     * 
     * @param scopeType the specified scope type
     */
    private ApplicationContext(final Class<? extends Annotation> scopeType) {
        super(scopeType);
    }
}
