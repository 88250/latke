package org.b3log.latke.servlet.handler;

/**
 * User: steveny
 * Date: 13-9-17
 * Time: 下午3:28
 */

import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.converter.ConvertSupport;

import java.lang.reflect.Method;

public class ProcessorInfo {

    private String[] pattern;
    private URIPatternMode uriPatternMode;
    private HTTPRequestMethod[] httpMethod;
    private Method invokeHolder;
    private Class<? extends ConvertSupport> convertClass;

    public void setPattern(String[] pattern) {
        this.pattern = pattern;
    }

    public String[] getPattern() {
        return pattern;
    }

    public void setUriPatternMode(URIPatternMode uriPatternMode) {
        this.uriPatternMode = uriPatternMode;
    }

    public URIPatternMode getUriPatternMode() {
        return uriPatternMode;
    }

    public void setHttpMethod(HTTPRequestMethod[] httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HTTPRequestMethod[] getHttpMethod() {
        return httpMethod;
    }

    public void setInvokeHolder(Method invokeHolder) {
        this.invokeHolder = invokeHolder;
    }

    public Method getInvokeHolder() {
        return invokeHolder;
    }

    public void setConvertClass(Class<? extends ConvertSupport> convertClass) {
        this.convertClass = convertClass;
    }

    public Class<? extends ConvertSupport> getConvertClass() {
        return convertClass;
    }
}