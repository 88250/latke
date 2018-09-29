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


import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.annotation.PathVariable;
import org.b3log.latke.servlet.converter.Converters;
import org.b3log.latke.util.Reflections;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * PrepareHandler: prepare the method args.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.2, Sep 26, 2013
 */
public class ArgsHandler implements Handler {

    /**
     * the method args data.
     */
    public static final String PREPARE_ARGS = "PREPARE_ARGS";

    @Override
    public void handle(final HTTPRequestContext context, final HttpControl httpControl) throws Exception {

        final MatchResult result = (MatchResult) httpControl.data(RequestDispatchHandler.MATCH_RESULT);
        final Method invokeHolder = result.getProcessorInfo().getInvokeHolder();

        final Map<String, Object> args = new LinkedHashMap<String, Object>();

        final Class<?>[] parameterTypes = invokeHolder.getParameterTypes();
        final String[] paramterNames = getParamterNames(invokeHolder);

        for (int i = 0; i < parameterTypes.length; i++) {
            doParamter(args, parameterTypes[i], paramterNames[i], context, result, i);
        }

        httpControl.data(PREPARE_ARGS, args);

        // do advice and real method invoke
        httpControl.nextHandler();
        
    }

    /**
     * do args convert.
     *
     * @param args          the method args
     * @param parameterType parameterType
     * @param paramterName  paramterName
     * @param context       HTTPRequestContext
     * @param result        MatchResult
     * @param sequence      the sequence of the param in methon
     */


    private void doParamter(final Map<String, Object> args, final Class<?> parameterType, final String paramterName, final HTTPRequestContext context, final MatchResult result, final int sequence) {

        final Object ret = Converters.doConvert(parameterType, paramterName, context, result, sequence);

        args.put(paramterName, ret);
    }

    /**
     * using PathVariable or reflection to get the getParamterNames in method.
     *
     * @param invokeMethond invokeMethond
     * @return the names of the params.
     */
    private String[] getParamterNames(final Method invokeMethond) {
        final String[] methodParamNames = Reflections.getMethodVariableNames(invokeMethond.getDeclaringClass(), invokeMethond.getName(),
            invokeMethond.getParameterTypes());
        int i = 0;

        // PathVariable will conver
        for (java.lang.annotation.Annotation[] annotations : invokeMethond.getParameterAnnotations()) {
            for (java.lang.annotation.Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    methodParamNames[i] = ((PathVariable) annotation).value();
                }
            }
            i++;
        }
        return methodParamNames;
    }
}
