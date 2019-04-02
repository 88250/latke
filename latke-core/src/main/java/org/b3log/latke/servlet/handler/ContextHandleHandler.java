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
package org.b3log.latke.servlet.handler;

import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.RequestContext;

import java.lang.reflect.Method;

/**
 * Invokes processing method ({@link org.b3log.latke.servlet.function.ContextHandler#handle(RequestContext)} or method annotated {@link org.b3log.latke.servlet.annotation.RequestProcessing}).
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 5, 2018
 * @since 2.4.34
 */
public class ContextHandleHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ContextHandleHandler.class);

    @Override
    public void handle(final RequestContext context) throws Exception {
        final MatchResult result = (MatchResult) context.attr(RouteHandler.MATCH_RESULT);
        final ContextHandlerMeta contextHandlerMeta = result.getContextHandlerMeta();
        final Method invokeHolder = contextHandlerMeta.getInvokeHolder();
        final BeanManager beanManager = BeanManager.getInstance();
        final Object classHolder = beanManager.getReference(invokeHolder.getDeclaringClass());
        invokeHolder.invoke(classHolder, context);
    }
}
