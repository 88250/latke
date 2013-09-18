package org.b3log.latke.servlet.handler;

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
 * Date: 13-9-18
 * Time: 上午10:12
 */
public class PrepareHandler implements Ihandler {

    public static final String PREPARE_ARGS = "PREPARE_ARGS";

    @Override
    public void handle(HTTPRequestContext context, HttpControl httpControl) throws Exception {

        MatchResult result = (MatchResult) httpControl.data(RequestMatchHandler.MATCH_RESULT);
        Method invokeHolder = result.getProcessorInfo().getInvokeHolder();

        final Map<String, Object> args = new LinkedHashMap<String, Object>();

        final Class<?>[] parameterTypes = invokeHolder.getParameterTypes();
        final String[] paramterNames = getParamterNames(invokeHolder);

        for (int i = 0; i < parameterTypes.length; i++) {
            doParamter(args, parameterTypes[i], paramterNames[i], context, result, i);
        }

        httpControl.data("PREPARE_ARGS",args);
        httpControl.nextHandler();
    }

    private void doParamter(Map<String, Object> args, Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) {

        Object ret = Converters.doConvert(parameterType, paramterName, context, result, sequence);

        args.put(paramterName, ret);
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
