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

import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.handler.MatchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * The params converts.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.1, Dec 3, 2018
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
    public static Object doConvert(final Class<?> parameterType, final String paramterName, final RequestContext context,
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
    Object convert(Class<?> parameterType, String paramterName, RequestContext context, MatchResult result, int sequence)
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
        if (parameterType.equals(RequestContext.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object convert(final Class<?> parameterType, final String paramterName, final RequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        return context;
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
    public Object convert(final Class<?> parameterType, final String paramterName, final RequestContext context,
                          final MatchResult result, final int sequence) throws Exception {
        String ret = result.getPathVars().get(paramterName);
        if (null != ret) {
            // the dafault sys-convert.
            return getConverter(result.getContextHandlerMeta().getConvertClass()).convert(paramterName, ret, parameterType);
        }

        final HttpServletRequest request = context.getRequest();
        ret = request.getParameter(paramterName);
        if (null != ret) {
            // the user-customer converter.
            return getConverter(result.getContextHandlerMeta().getConvertClass()).convert(paramterName, ret, parameterType);
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
