package org.b3log.latke.servlet.converter;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.annotation.PathVariable;
import org.b3log.latke.servlet.annotation.Render;
import org.b3log.latke.servlet.handler.MatchResult;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;

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
        //first for special-class-convert(mainly for context) then name-matched-convert
        registerConverters(new ContextConvert());
        registerConverters(new RequestConvert());
        registerConverters(new ResponseConvert());
        registerConverters(new RendererConvert());
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
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
        if (AbstractHTTPResponseRenderer.class.isAssignableFrom(parameterType)
                && !parameterType.equals(AbstractHTTPResponseRenderer.class)) {
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
        if (ret != null) {
            // re-design
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
        //do not cache
        return ret;
    }
}
