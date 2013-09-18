/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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


import org.b3log.latke.Keys;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;
import org.b3log.latke.servlet.advice.BeforeRequestProcessAdvice;
import org.b3log.latke.servlet.advice.RequestProcessAdviceException;
import org.b3log.latke.servlet.advice.RequestReturnAdviceException;
import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * the handler to do the advice work in configs.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
public class AdviceHandler implements Ihandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdviceHandler.class.getName());

    @Override
    public void handle(final HTTPRequestContext context, final HttpControl httpControl) throws Exception {

        // the data which pre-handler provided.
        final MatchResult result = (MatchResult) httpControl.data(RequestMatchHandler.MATCH_RESULT);
        final Map<String, Object> args = (Map<String, Object>) httpControl.data(PrepareAndExecuteHandler.PREPARE_ARGS);

        final Method invokeHolder = result.getProcessorInfo().getInvokeHolder();
        final Class<?> processorClass = invokeHolder.getDeclaringClass();
        final List<AbstractHTTPResponseRenderer> rendererList = result.getRendererList();

        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();

        final List<Class<? extends BeforeRequestProcessAdvice>> beforeAdviceClassList = getBeforeList(invokeHolder, processorClass);

        try {
            BeforeRequestProcessAdvice binstance = null;

            for (Class<? extends BeforeRequestProcessAdvice> clz : beforeAdviceClassList) {
                binstance = beanManager.getReference(clz);
                binstance.doAdvice(context, args);
            }
        } catch (final RequestReturnAdviceException re) {
            return;
        } catch (final RequestProcessAdviceException e) {
            final JSONObject exception = e.getJsonObject();

            LOGGER.log(Level.WARN, "Occurs an exception before request processing [errMsg={0}]", exception.optString(Keys.MSG));
            final JSONRenderer ret = new JSONRenderer();

            ret.setJSONObject(exception);
            context.setRenderer(ret);
            return;
        }

        for (AbstractHTTPResponseRenderer renderer : rendererList) {
            renderer.preRender(context, args);
        }

        httpControl.nextHandler();

        for (int j = rendererList.size() - 1; j >= 0; j--) {
            rendererList.get(j).postRender(context, httpControl.data(MethodInvokeHandler.INVOKE_RESULT));
        }

        final List<Class<? extends AfterRequestProcessAdvice>> afterAdviceClassList = getAfterList(invokeHolder, processorClass);
        AfterRequestProcessAdvice instance;

        for (Class<? extends AfterRequestProcessAdvice> clz : afterAdviceClassList) {
            instance = beanManager.getReference(clz);
            instance.doAdvice(context, httpControl.data(MethodInvokeHandler.INVOKE_RESULT));
        }
    }

    /**
     * get BeforeRequestProcessAdvice from annotation.
     *
     * @param invokeHolder   the real invoked method
     * @param processorClass the class of the invoked methond
     * @return the list of BeforeRequestProcessAdvice
     */
    private List<Class<? extends BeforeRequestProcessAdvice>> getBeforeList(final Method invokeHolder, final Class<?> processorClass) {
        // before invoke(first class before advice and then method before advice).
        final List<Class<? extends BeforeRequestProcessAdvice>> beforeAdviceClassList = new ArrayList<Class<? extends BeforeRequestProcessAdvice>>();

        if (processorClass.isAnnotationPresent(Before.class)) {
            final Class<? extends BeforeRequestProcessAdvice>[] ac = processorClass.getAnnotation(Before.class).adviceClass();

            beforeAdviceClassList.addAll(Arrays.asList(ac));
        }
        if (invokeHolder.isAnnotationPresent(Before.class)) {
            final Class<? extends BeforeRequestProcessAdvice>[] ac = invokeHolder.getAnnotation(Before.class).adviceClass();

            beforeAdviceClassList.addAll(Arrays.asList(ac));
        }

        return beforeAdviceClassList;
    }

    /**
     * get AfterRequestProcessAdvice from annotation.
     *
     * @param invokeHolder   the real invoked method
     * @param processorClass the class of the invoked methond
     * @return the list of AfterRequestProcessAdvice
     */
    private List<Class<? extends AfterRequestProcessAdvice>> getAfterList(final Method invokeHolder, final Class<?> processorClass) {
        // after invoke(first method before advice and then class before advice).
        final List<Class<? extends AfterRequestProcessAdvice>> afterAdviceClassList = new ArrayList<Class<? extends AfterRequestProcessAdvice>>();

        if (invokeHolder.isAnnotationPresent(After.class)) {
            final Class<? extends AfterRequestProcessAdvice>[] ac = invokeHolder.getAnnotation(After.class).adviceClass();

            afterAdviceClassList.addAll(Arrays.asList(ac));
        }

        if (processorClass.isAnnotationPresent(After.class)) {
            final Class<? extends AfterRequestProcessAdvice>[] ac = processorClass.getAnnotation(After.class).adviceClass();

            afterAdviceClassList.addAll(Arrays.asList(ac));
        }

        return afterAdviceClassList;
    }
}

