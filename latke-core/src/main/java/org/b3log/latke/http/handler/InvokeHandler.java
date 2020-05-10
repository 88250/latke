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
 * @version 2.0.0.2, May 1, 2020
 * @since 2.4.34
 */
public class InvokeHandler implements Handler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(InvokeHandler.class);

    @Override
    public void handle(final RequestContext context) {
        final RouteResolution result = (RouteResolution) context.attr(RequestContext.MATCH_RESULT);
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
