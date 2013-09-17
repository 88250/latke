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
package org.b3log.latke.servlet.converter;


import org.apache.commons.lang.StringUtils;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.annotation.PathVariable;
import org.b3log.latke.servlet.annotation.Render;
import org.b3log.latke.servlet.handler.MatchResult;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * User: mainlove
 * Date: 13-9-15
 * Time: 下午6:38
 */
public class Converters {

    private static final List<IConverters> convertersList = new ArrayList<IConverters>();

    static {
        // first for special-class-convert(mainly for context) then name-matched-convert
        registerConverters(new ContextConvert());
        registerConverters(new RequestConvert());
        registerConverters(new ResponseConvert());
        registerConverters(new RendererConvert());
        registerConverters(new JSONObjectConvert());
        registerConverters(new PathVariableConvert());

    }

    public static void registerConverters(IConverters converter) {
        convertersList.add(converter);
    }

    public static Object doConvert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) {

        for (IConverters iConverters : convertersList) {
            if (iConverters.isMatched(parameterType, paramterName)) {
                try {
                    return iConverters.convert(parameterType, paramterName, context, result, sequence);
                } catch (Exception e) {
                    e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        return null;
    }

}


interface IConverters {

    Boolean isMatched(Class<?> parameterType, String paramterName);

    Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception;

}


class ContextConvert implements IConverters {
    @Override
    public Boolean isMatched(Class<?> parameterType, String paramterName) {
        if (parameterType.equals(HTTPRequestContext.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception {
        return context;
    }
}


class RequestConvert implements IConverters {
    @Override
    public Boolean isMatched(Class<?> parameterType, String paramterName) {
        if (parameterType.equals(HttpServletRequest.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception {
        return context.getRequest();
    }
}


class ResponseConvert implements IConverters {
    @Override
    public Boolean isMatched(Class<?> parameterType, String paramterName) {
        if (parameterType.equals(HttpServletResponse.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception {
        return context.getResponse();
    }
}


class RendererConvert implements IConverters {
    @Override
    public Boolean isMatched(Class<?> parameterType, String paramterName) {
        if (AbstractHTTPResponseRenderer.class.isAssignableFrom(parameterType) && !parameterType.equals(AbstractHTTPResponseRenderer.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception {

        final AbstractHTTPResponseRenderer ins = (AbstractHTTPResponseRenderer) parameterType.newInstance();
        final String rid = getRendererId(result.getInvokeMethod().getDeclaringClass(), result.getInvokeMethod(), sequence);

        ins.setRendererId(rid);
        result.addRenders(ins);
        return ins;
    }

    /**
     * getRendererId from mark {@link org.b3log.latke.servlet.annotation.Render},using"-" as split:class_method_PARAMETER.
     *
     * @param processorClass  class
     * @param processorMethod method
     * @param i               the index of the
     * @return string
     */
    private static String getRendererId(final Class<?> processorClass, final Method processorMethod, final int i) {
        final StringBuilder sb = new StringBuilder();

        if (processorClass.isAnnotationPresent(Render.class)) {
            final String v = processorClass.getAnnotation(Render.class).value();

            if (StringUtils.isNotBlank(v)) {
                sb.append(v).append(v);
            }
        }

        if (processorMethod.isAnnotationPresent(Render.class)) {

            final String v = processorClass.getAnnotation(Render.class).value();

            if (StringUtils.isNotBlank(v)) {
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(v).append(v);
            }
        }

        for (java.lang.annotation.Annotation annotation : processorMethod.getParameterAnnotations()[i]) {
            if (annotation instanceof Render) {
                final String v = ((PathVariable) annotation).value();

                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(v).append(v);
            }
        }

        return sb.toString();
    }

}


class PathVariableConvert implements IConverters {
    @Override
    public Boolean isMatched(Class<?> parameterType, String paramterName) {
        return true;
    }

    @Override
    public Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception {

        Object ret = result.getMapValues().get(paramterName);

        if (ret != null) {// re-design
            return getConverter(result.getProcessorInfo().getConvertClass())
                    .convert(paramterName, ret, parameterType);
        }

        HttpServletRequest request = context.getRequest();
        ret = (request.getParameter(paramterName));
        if (ret != null) {// re-design
            return getConverter(result.getProcessorInfo().getConvertClass())
                    .convert(paramterName, ret, parameterType);
        }

        return null;
    }

    /**
     * get the converter in this method,using cache.
     *
     * @param convertClass the class of {@link ConvertSupport}
     * @return {@link ConvertSupport}
     * @throws Exception Exception
     */
    private static ConvertSupport getConverter(final Class<? extends ConvertSupport> convertClass) throws Exception {
        ConvertSupport ret = convertClass.newInstance();

        // do not cache
        return ret;
    }
}


class JSONObjectConvert implements IConverters {

    @Override
    public Boolean isMatched(Class<?> parameterType, String paramterName) {

        if (parameterType.equals(JSONObject.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence) throws Exception {

        JSONObject ret = new JSONObject();

        HttpServletRequest request = context.getRequest();
        for (Object o : request.getParameterMap().keySet()) {
            ret.put(String.valueOf(o), request.getParameterMap().get(o));
        }
        //
        for (String key : result.getMapValues().keySet()) {
            ret.put(key, result.getMapValues().get(key));
        }
        return ret;
    }
}

