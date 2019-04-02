/*
 * Copyright (c) 2009-present, b3log.org
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

import org.b3log.latke.servlet.HttpMethod;

import java.lang.annotation.*;

/**
 * Indicates that an annotated method for HTTP servlet request processing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.7, Dec 10, 2018
 * @see RequestProcessor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestProcessing {

    /**
     * The dispatching URI templates of a request.
     *
     * @return values
     */
    String[] value() default {};

    /**
     * The HTTP request methods the annotated method should process.
     *
     * @return HTTP methods
     */
    HttpMethod[] method() default {HttpMethod.GET};
}
