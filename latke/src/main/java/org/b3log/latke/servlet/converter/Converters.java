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
package org.b3log.latke.servlet.converter;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.annotation.PathVariable;
import org.b3log.latke.servlet.annotation.Render;
import org.b3log.latke.servlet.handler.MatchResult;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.util.Requests;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The params converts.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Feb 24, 2018
 */
public final class Converters {

    /**
     * CONVERTERS_LIST holder.
     */
    private static final List<IConverters> CONVERTERS_LIST = new ArrayList<>();

    /**
     * Private constructor.
     */
    private Converters() {
    }

    static {
        // first for special-class-convert(mainly for context) then
        // name-matched-convert
        registerConverters(new ContextConvert());
        registerConverters(new RequestConvert());
        registerConverters(new ResponseConvert());
        registerConverters(new RendererConvert());
        registerConverters(new JSONObjectConvert());
        registerConverters(new RequestJSONObjectConvert());

        // The path variable converter must be the last one
        registerConverters(new PathVariableConvert());
    }

    /**
     * registerConverters.
     *
     * @param converter converter
     */
    public static void registerConverters(final IConverters converter) {
        CONVERTERS_LIST.add(converter);
    }

    /**
     * doConvert one by one.
     *
     * @param parameterType parameterType
     * @param paramterName  paramterName
     * @param context       HTTPRequestContext
     * @param result        MatchResult
     * @param sequence      sequence
     * @return ret
     */
    public static Object doConvert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                                   final MatchResult result, final int sequence) {
        for (final IConverters iConverters : CONVERTERS_LIST) {
            if (iConverters.isMatched(parameterType, paramterName)) {
                try {
                    return iConverters.convert(parameterType, paramterName, context, result, sequence);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}

/**
 * the interface of the converter.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
interface IConverters {

    /**
     * should be converted.
     *
     * @param parameterType parameterType
     * @param paramterName  paramterName
     * @return isMatched
     */
    Boolean isMatched(Class<?> parameterType, String paramterName);

    /**
     * do Real-convert.
     *
     * @param parameterType parameterType
     * @param paramterName  paramterName
     * @param context       HTTPRequestContext
     * @param result        MatchResult
     * @param sequence      sequence
     * @return ret
     * @throws Exception the convert-Exception
     */
    Object convert(Class<?> parameterType, String paramterName, HTTPRequestContext context, MatchResult result, int sequence)
            throws Exception;
}

/**
 * to inject HTTPRequestContext.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
class ContextConvert implements IConverters {
    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        if (parameterType.equals(HTTPRequestContext.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        return context;
    }
}

/**
 * to inject  HttpServletRequest.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
class RequestConvert implements IConverters {
    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        if (parameterType.equals(HttpServletRequest.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        return context.getRequest();
    }
}

/**
 * to inject HttpServletResponse.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
class ResponseConvert implements IConverters {
    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        if (parameterType.equals(HttpServletResponse.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        return context.getResponse();
    }
}

/**
 * to init and inject AbstractHTTPResponseRenderer.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
class RendererConvert implements IConverters {
    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        if (AbstractHTTPResponseRenderer.class.isAssignableFrom(parameterType) && !parameterType.equals(AbstractHTTPResponseRenderer.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {

        final AbstractHTTPResponseRenderer ins = (AbstractHTTPResponseRenderer) parameterType.newInstance();
        final String rid = getRendererId(result.getProcessorInfo().getInvokeHolder().getDeclaringClass(),
                result.getProcessorInfo().getInvokeHolder(), sequence);

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

/**
 * the default PathVariable name-matched convert.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
class PathVariableConvert implements IConverters {
    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        return true;
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {

        Object ret = result.getMapValues().get(paramterName);

        if (ret != null) {
            // the dafault sys-convert.
            return getConverter(result.getProcessorInfo().getConvertClass()).convert(paramterName, ret, parameterType);
        }

        final HttpServletRequest request = context.getRequest();

        ret = request.getParameter(paramterName);
        if (ret != null) {
            // the user-customer converter.
            return getConverter(result.getProcessorInfo().getConvertClass()).convert(paramterName, ret, parameterType);
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
        final ConvertSupport ret = convertClass.newInstance();

        // do not cache
        return ret;
    }
}

/**
 * to store request-params in jsonObject.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Feb 24, 2018
 */
class JSONObjectConvert implements IConverters {

    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        return parameterType.equals(JSONObject.class) && !"requestJSONObject".equals(paramterName);
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        final JSONObject ret = new JSONObject();

        final HttpServletRequest request = context.getRequest();
        for (Object o : request.getParameterMap().keySet()) {
            ret.put(String.valueOf(o), request.getParameterMap().get(o));
        }

        // mapValue will cover
        for (String key : result.getMapValues().keySet()) {
            ret.put(key, result.getMapValues().get(key));
        }

        return ret;
    }
}

/**
 * To store request body json with requestJSONObject. https://github.com/b3log/latke/issues/76
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Feb 24, 2018
 */
class RequestJSONObjectConvert implements IConverters {

    @Override
    public Boolean isMatched(final Class<?> parameterType, final String paramterName) {
        return parameterType.equals(JSONObject.class) && "requestJSONObject".equals(paramterName);
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final HTTPRequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        return Requests.parseRequestJSONObject(context.getRequest(), context.getResponse());
    }
}
