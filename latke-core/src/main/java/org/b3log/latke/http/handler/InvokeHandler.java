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
package org.b3log.latke.http.handler;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.function.Handler;
import org.b3log.latke.ioc.BeanManager;

import java.lang.reflect.Method;

/**
 * Invokes processing method ({@link Handler#handle(RequestContext)}.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 12, 2020
 * @since 2.4.34
 */
public class InvokeHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(InvokeHandler.class);

    @Override
    public void handle(final RequestContext context) {
        final RouteResolution result = (RouteResolution) context.attr(RouteHandler.MATCH_RESULT);
        final ContextHandlerMeta contextHandlerMeta = result.getContextHandlerMeta();
        final Method invokeHolder = contextHandlerMeta.getInvokeHolder();
        final BeanManager beanManager = BeanManager.getInstance();
        final Object classHolder = beanManager.getReference(invokeHolder.getDeclaringClass());
        try {
            invokeHolder.invoke(classHolder, context);
        } catch (final Throwable e) {
            LOGGER.log(Level.ERROR, "Handler processing failed: ", e);
            context.sendError(500);
            context.abort();

            return;
        }

        context.handle();
    }
}
