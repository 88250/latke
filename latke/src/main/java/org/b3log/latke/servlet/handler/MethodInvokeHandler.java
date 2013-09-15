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


import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HttpControl;
import org.b3log.latke.servlet.annotation.PathVariable;
import org.b3log.latke.servlet.converter.Converters;
import org.b3log.latke.util.ReflectHelper;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: steveny
 * Date: 13-9-12
 * Time: 下午4:30
 */
public class MethodInvokeHandler implements Ihandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MethodInvokeHandler.class.getName());

    /**
     * the shared-invoke-result-data name.
     */
    public static final String INVOKE_RESULT = "INVOKE_RESULT";


    @Override
    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception {

        MatchResult result = (MatchResult) httpControl.data(RequestMatchHandler.MATCH_RESULT);
        Method invokeHolder = result.getInvokeMethod();

        //get class instance
        final LatkeBeanManager beanManager = Lifecycle.getBeanManager();
        final Object classHolder = beanManager.getReference(invokeHolder.getDeclaringClass());

        final Map<String, Object> args = new LinkedHashMap<String, Object>();

        final Class<?>[] parameterTypes = invokeHolder.getParameterTypes();
        final String[] paramterNames = getParamterNames(invokeHolder);
        for (int i = 0; i < parameterTypes.length; i++) {
            doParamter(args, parameterTypes[i], paramterNames[i], context, result,i);
        }

        final Object ret = invokeHolder.invoke(classHolder, args.values().toArray());

        httpControl.data(INVOKE_RESULT, ret);
        httpControl.nextHandler();
    }

    private void doParamter(Map<String, Object> args, Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) {

        Object ret = Converters.doConvert(parameterType, paramterName, context, result,sequence);
        args.put(paramterName,ret);
    }

    private String[] getParamterNames(Method invokeMethond) {
        String[] methodParamNames = ReflectHelper.getMethodVariableNames(invokeMethond.getDeclaringClass(), invokeMethond.getName(),
                invokeMethond.getParameterTypes());
        int i = 0;

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
