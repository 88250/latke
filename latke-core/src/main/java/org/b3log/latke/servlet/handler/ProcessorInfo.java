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
package org.b3log.latke.servlet.handler;

import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.servlet.HttpRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.converter.ConvertSupport;
import org.b3log.latke.servlet.function.ContextHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcessorInfo,which store the processor-annotation info.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Dec 2, 2018
 */
public final class ProcessorInfo {

    /**
     * Patterns.
     */
    private String[] pattern;

    /**
     * URIPatternMode.
     */
    private URIPatternMode uriPatternMode;

    /**
     * HTTP methods.
     */
    private HttpRequestMethod[] httpMethod;

    /**
     * The processor method.
     */
    private Method invokeHolder;

    /**
     * Context handler if using functional routing.
     */
    private ContextHandler handler;

    /**
     * The param-convert configs.
     */
    private Class<? extends ConvertSupport> convertClass;

    /**
     * Before request process advices.
     */
    private List<BeforeRequestProcessAdvice> beforeRequestProcessAdvices;

    /**
     * After request process advices.
     */
    private List<AfterRequestProcessAdvice> afterRequestProcessAdvices;

    /**
     * Set the before request process advices.
     *
     * @param beforeRequestProcessAdvices the specified before request process advices
     */
    public void setBeforeRequestProcessAdvices(final List<BeforeRequestProcessAdvice> beforeRequestProcessAdvices) {
        this.beforeRequestProcessAdvices = beforeRequestProcessAdvices;
    }

    /**
     * Get the before request process advices.
     *
     * @return before request process advices
     */
    public List<BeforeRequestProcessAdvice> getBeforeRequestProcessAdvices() {
        return beforeRequestProcessAdvices;
    }

    /**
     * Set the after request process advices.
     *
     * @param afterRequestProcessAdvices the specified after request process advices
     */
    public void setAfterRequestProcessAdvices(final List<AfterRequestProcessAdvice> afterRequestProcessAdvices) {
        this.afterRequestProcessAdvices = afterRequestProcessAdvices;
    }

    /**
     * Get the after request process advices.
     *
     * @return after request process advices
     */
    public List<AfterRequestProcessAdvice> getAfterRequestProcessAdvices() {
        return afterRequestProcessAdvices;
    }

    /**
     * setPattern.
     *
     * @param pattern pattern
     */
    public void setPattern(final String[] pattern) {
        this.pattern = pattern;
    }

    /**
     * getPattern.
     *
     * @return pattern
     */
    public String[] getPattern() {
        return pattern;
    }

    /**
     * setUriPatternMode.
     *
     * @param uriPatternMode uriPatternMode
     */
    public void setUriPatternMode(final URIPatternMode uriPatternMode) {
        this.uriPatternMode = uriPatternMode;
    }

    /**
     * getUriPatternMode.
     *
     * @return uriPatternMode
     */
    public URIPatternMode getUriPatternMode() {
        return uriPatternMode;
    }

    /**
     * setHttpMethod.
     *
     * @param httpMethod httpMethod
     */
    public void setHttpMethod(final HttpRequestMethod[] httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * getHttpMethod.
     *
     * @return httpMethod
     */
    public HttpRequestMethod[] getHttpMethod() {
        return httpMethod;
    }

    /**
     * setInvokeHolder.
     *
     * @param invokeHolder invokeHolder
     */
    public void setInvokeHolder(final Method invokeHolder) {
        this.invokeHolder = invokeHolder;
    }

    /**
     * getInvokeHolder.
     *
     * @return invokeHolder
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
     * setConvertClass.
     *
     * @param convertClass convertClass
     */
    public void setConvertClass(final Class<? extends ConvertSupport> convertClass) {
        this.convertClass = convertClass;
    }

    /**
     * getConvertClass.
     *
     * @return convertClass
     */
    public Class<? extends ConvertSupport> getConvertClass() {
        return convertClass;
    }

    /**
     * Get before process advices.
     *
     * @param processorInfo the specified process info
     * @return before request process advices
     */
    public static List<BeforeRequestProcessAdvice> getBeforeList(final ProcessorInfo processorInfo) {
        final List<BeforeRequestProcessAdvice> ret = new ArrayList<>();

        final Method invokeHolder = processorInfo.getInvokeHolder();
        final Class<?> processorClass = invokeHolder.getDeclaringClass();

        // 1. process class advice
        if (null != processorClass && processorClass.isAnnotationPresent(Before.class)) {
            final Class<? extends BeforeRequestProcessAdvice>[] bcs = processorClass.getAnnotation(Before.class).adviceClass();
            for (int i = 0; i < bcs.length; i++) {
                final Class<? extends BeforeRequestProcessAdvice> bc = bcs[i];
                final BeforeRequestProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(bc);
                ret.add(beforeRequestProcessAdvice);
            }
        }
        // 2. process method advice
        if (invokeHolder.isAnnotationPresent(Before.class)) {
            final Class<? extends BeforeRequestProcessAdvice>[] bcs = invokeHolder.getAnnotation(Before.class).adviceClass();
            for (int i = 0; i < bcs.length; i++) {
                final Class<? extends BeforeRequestProcessAdvice> bc = bcs[i];
                final BeforeRequestProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(bc);
                ret.add(beforeRequestProcessAdvice);
            }
        }

        return ret;
    }

    /**
     * Get after process advices.
     *
     * @param processorInfo the specified process info
     * @return after request process advices
     */
    public static List<AfterRequestProcessAdvice> getAfterList(final ProcessorInfo processorInfo) {
        final List<AfterRequestProcessAdvice> ret = new ArrayList<>();

        final Method invokeHolder = processorInfo.getInvokeHolder();
        final Class<?> processorClass = invokeHolder.getDeclaringClass();

        // 1. process method advice
        if (invokeHolder.isAnnotationPresent(After.class)) {
            final Class<? extends AfterRequestProcessAdvice>[] acs = invokeHolder.getAnnotation(After.class).adviceClass();
            for (int i = 0; i < acs.length; i++) {
                final Class<? extends AfterRequestProcessAdvice> ac = acs[i];
                final AfterRequestProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(ac);
                ret.add(beforeRequestProcessAdvice);
            }
        }
        // 2. process class advice
        if (null != processorClass && processorClass.isAnnotationPresent(After.class)) {
            final Class<? extends AfterRequestProcessAdvice>[] acs = invokeHolder.getAnnotation(After.class).adviceClass();
            for (int i = 0; i < acs.length; i++) {
                final Class<? extends AfterRequestProcessAdvice> ac = acs[i];
                final AfterRequestProcessAdvice beforeRequestProcessAdvice = BeanManager.getInstance().getReference(ac);
                ret.add(beforeRequestProcessAdvice);
            }
        }

        return ret;
    }
}
