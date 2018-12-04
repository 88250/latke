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
package org.b3log.latke.servlet.annotation;

import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.latke.servlet.converter.ConvertSupport;

import java.lang.annotation.*;

/**
 * Indicates that an annotated method for HTTP servlet request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.0.0.5, Dec 23, 2015
 * @see RequestProcessor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestProcessing {

    /**
     * The dispatching URI path patterns of a request.
     *
     * <p>
     * Semantics of these values adapting to the URL patterns (&lt;url-pattern/&gt;) configures in web application
     * descriptor (web.xml) of a servlet. Ant-style path pattern and regular expression pattern are also supported.
     * </p>
     * 
     * @return values
     */
    String[] value() default {};

    /**
     * The URI patterns mode.
     * 
     * @return URI patterns mode
     */
    URIPatternMode uriPatternsMode() default URIPatternMode.ANT_PATH;

    /**
     * The HTTP request methods the annotated method should process.
     * 
     * @return HTTP request methods
     */
    HTTPRequestMethod[] method() default {HTTPRequestMethod.GET};

    /**
     * User customized data convert class.
     * 
     * @return convert class
     */
    Class<? extends ConvertSupport> convertClass() default ConvertSupport.class;
}
