/*
 * Copyright (c) 2009-2017, b3log.org & hacpai.com
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


import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.converter.ConvertSupport;

import java.lang.reflect.Method;


/**
 * ProcessorInfo,which store the processor-annotation info.
 *
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.1, Sep 18, 2013
 */
public class ProcessorInfo {

    /**
     *  patterns in Processor.
     */
    private String[] pattern;

    /**
     *URIPatternMode.
     */
    private URIPatternMode uriPatternMode;

    /**
     * http methods.
     */
    private HTTPRequestMethod[] httpMethod;

    /**
     * the real Method holder.
     */
    private Method invokeHolder;

    /**
     * the param-convert configs.
     */
    private Class<? extends ConvertSupport> convertClass;

    /**
     * setPattern.
     * @param pattern pattern
     */
    public void setPattern(final String[] pattern) {
        this.pattern = pattern;
    }

    /**
     * getPattern.
     * @return pattern
     */
    public String[] getPattern() {
        return pattern;
    }

    /**
     * setUriPatternMode.
     * @param uriPatternMode uriPatternMode
     */
    public void setUriPatternMode(final URIPatternMode uriPatternMode) {
        this.uriPatternMode = uriPatternMode;
    }

    /**
     * getUriPatternMode.
     * @return  uriPatternMode
     */
    public URIPatternMode getUriPatternMode() {
        return uriPatternMode;
    }

    /**
     * setHttpMethod.
     * @param httpMethod httpMethod
     */
    public void setHttpMethod(final HTTPRequestMethod[] httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * getHttpMethod.
     * @return httpMethod
     */
    public HTTPRequestMethod[] getHttpMethod() {
        return httpMethod;
    }

    /**
     * setInvokeHolder.
     * @param invokeHolder  invokeHolder
     */
    public void setInvokeHolder(final Method invokeHolder) {
        this.invokeHolder = invokeHolder;
    }

    /**
     * getInvokeHolder.
     * @return invokeHolder
     */
    public Method getInvokeHolder() {
        return invokeHolder;
    }

    /**
     * setConvertClass.
     * @param convertClass convertClass
     */
    public void setConvertClass(final Class<? extends ConvertSupport> convertClass) {
        this.convertClass = convertClass;
    }

    /**
     * getConvertClass.
     * @return  convertClass
     */
    public Class<? extends ConvertSupport> getConvertClass() {
        return convertClass;
    }
}
