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

import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.converter.Converters;
import org.b3log.latke.servlet.function.ContextHandler;
import org.b3log.latke.util.Reflections;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PrepareHandler: prepare the method args.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Dec 2, 2018
 */
public class ArgsHandler implements Handler {

    /**
     * the method args data.
     */
    public static final String PREPARE_ARGS = "PREPARE_ARGS";

    @Override
    public void handle(final RequestContext context, final HttpControl httpControl) {
        final MatchResult result = (MatchResult) httpControl.data(RequestDispatchHandler.MATCH_RESULT);
        final ProcessorInfo processorInfo = result.getProcessorInfo();
        final ContextHandler handler = processorInfo.getHandler();
        final Map<String, Object> args = new LinkedHashMap<>();
        if (null != handler) {
            doParamter(args, RequestContext.class, "context", context, result, 0);
        } else {
            final Method invokeHolder = processorInfo.getInvokeHolder();
            final Class<?>[] parameterTypes = invokeHolder.getParameterTypes();
            final String[] methodVariableNames = Reflections.getMethodVariableNames(
                    invokeHolder.getDeclaringClass(), invokeHolder.getName(), invokeHolder.getParameterTypes());
            for (int i = 0; i < parameterTypes.length; i++) {
                doParamter(args, parameterTypes[i], methodVariableNames[i], context, result, i);
            }
        }

        httpControl.data(PREPARE_ARGS, args);

        httpControl.nextHandler();
    }

    /**
     * do args convert.
     *
     * @param args          the method args
     * @param parameterType parameterType
     * @param paramterName  paramterName
     * @param context       RequestContext
     * @param result        MatchResult
     * @param sequence      the sequence of the param in methon
     */
    private void doParamter(final Map<String, Object> args, final Class<?> parameterType, final String paramterName, final RequestContext context, final MatchResult result, final int sequence) {
        final Object ret = Converters.doConvert(parameterType, paramterName, context, result, sequence);

        args.put(paramterName, ret);
    }
}
