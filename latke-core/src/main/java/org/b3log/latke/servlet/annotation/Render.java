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


import java.lang.annotation.*;


/**
 * The render mark for the parameter of the method, just to set the id of the renderer.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @version 1.0.0.0, Jan 21, 2013
 */
@Target({ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Render {

    /**
     * The id of the render, for plugin to identify.
     *
     * @return value
     */
    String value();
}
