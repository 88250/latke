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
import org.b3log.latke.servlet.HttpMethod;
import org.b3log.latke.servlet.advice.ProcessAdvice;
import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.function.ContextHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Context handler metadata.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Dec 10, 2018
 * @since 2.4.34
 */
public final class ContextHandlerMeta {

    /**
     * URI templates.
     */
    private String[] uriTemplates;

    /**
     * HTTP methods.
     */
    private HttpMethod[] httpMethods;

    /**
     * The processor method.
     */
    private Method invokeHolder;

    /**
     * Context handler if using functional routing.
     */
    private ContextHandler handler;

    /**
     * Before request process advices.
     */
    private List<ProcessAdvice> beforeRequestProcessAdvices;

    /**
     * After request process advices.
     */
    private List<ProcessAdvice> afterRequestProcessAdvices;

    /**
     * Get the before request process advices.
     *
     * @return before request process advices
     */
    public List<ProcessAdvice> getBeforeRequestProcessAdvices() {
        return beforeRequestProcessAdvices;
    }

    /**
     * Get the after request process advices.
     *
     * @return after request process advices
     */
    public List<ProcessAdvice> getAfterRequestProcessAdvices() {
        return afterRequestProcessAdvices;
    }

    /**
     * Initializes process advices.
     */
    public void initProcessAdvices() {
        initBeforeList();
        initAfterList();
    }

    /**
     * Set the URI templates with the specified URI templates.
     *
     * @param uriTemplates the specified URI templates
     */
    public void setUriTemplates(final String[] uriTemplates) {
        this.uriTemplates = uriTemplates;
    }

    /**
     * Get the URI templates.
     *
     * @return URI templates
     */
    public String[] getUriTemplates() {
        return uriTemplates;
    }

    /**
     * Sets the HTTP methods with the specified HTTP methods.
     *
     * @param httpMethods httpMethods the specified HTTP methods
     */
    public void setHttpMethods(final HttpMethod[] httpMethods) {
        this.httpMethods = httpMethods;
    }

    /**
     * Get the HTTP methods.
     *
     * @return HTTP methods
     */
    public HttpMethod[] getHttpMethods() {
        return httpMethods;
    }

    /**
     * Sets the invoke holder with the specified invoke holder.
     *
     * @param invokeHolder invokeHolder the specified invoke holder
     */
    public void setInvokeHolder(final Method invokeHolder) {
        this.invokeHolder = invokeHolder;
    }

    /**
     * Gets the invoke holder.
     *
     * @return invoke holder
     */
    public Method getInvokeHolder() {
        return invokeHolder;
    }

    /**
     * Sets the context handler.
     *
     * @param handler the specified handler
     */
    public void setHandler(final ContextHandler handler) {
        this.handler = handler;
    }

    /**
     * Gets the context handler.
     *
     * @return handler
     */
    public ContextHandler getHandler() {
        return handler;
    }

    /**
     * Initializes before process advices.
     */
    private void initBeforeList() {
        final List<ProcessAdvice> beforeRequestProcessAdvices = new ArrayList<>();

        final Method invokeHolder = getInvokeHolder();
        final Class<?> processorClass = invokeHolder.getDeclaringClass();

        // 1. process class advice
        if (null != processorClass && processorClass.isAnnotationPresent(Before.class)) {
            final Class<? extends ProcessAdvice>[] bcs = processorClass.getAnnotation(Before.class).value();
            for (int i = 0; i < bcs.length; i++) {
                final Class<? extends ProcessAdvice> bc = bcs[i];
                final ProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(bc);
                beforeRequestProcessAdvices.add(beforeRequestProcessAdvice);
            }
        }
        // 2. process method advice
        if (invokeHolder.isAnnotationPresent(Before.class)) {
            final Class<? extends ProcessAdvice>[] bcs = invokeHolder.getAnnotation(Before.class).value();
            for (int i = 0; i < bcs.length; i++) {
                final Class<? extends ProcessAdvice> bc = bcs[i];
                final ProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(bc);
                beforeRequestProcessAdvices.add(beforeRequestProcessAdvice);
            }
        }

        this.beforeRequestProcessAdvices = beforeRequestProcessAdvices;
    }

    /**
     * Initializes after process advices.
     */
    private void initAfterList() {
        final List<ProcessAdvice> afterRequestProcessAdvices = new ArrayList<>();

        final Method invokeHolder = getInvokeHolder();
        final Class<?> processorClass = invokeHolder.getDeclaringClass();

        // 1. process method advice
        if (invokeHolder.isAnnotationPresent(After.class)) {
            final Class<? extends ProcessAdvice>[] acs = invokeHolder.getAnnotation(After.class).value();
            for (int i = 0; i < acs.length; i++) {
                final Class<? extends ProcessAdvice> ac = acs[i];
                final ProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(ac);
                afterRequestProcessAdvices.add(beforeRequestProcessAdvice);
            }
        }
        // 2. process class advice
        if (null != processorClass && processorClass.isAnnotationPresent(After.class)) {
            final Class<? extends ProcessAdvice>[] acs = invokeHolder.getAnnotation(After.class).value();
            for (int i = 0; i < acs.length; i++) {
                final Class<? extends ProcessAdvice> ac = acs[i];
                final ProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(ac);
                afterRequestProcessAdvices.add(beforeRequestProcessAdvice);
            }
        }

        this.afterRequestProcessAdvices = afterRequestProcessAdvices;
    }
}
