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


import org.b3log.latke.servlet.advice.AfterRequestProcessAdvice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The after advice mark for processMethod(for both processClass and processMethod).
 * 
 * @author <a href="mailto:wmainlove@gmail.com">Love Yao</a>
 * @version 1.1.0.0, Jan 17, 2013
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface After {

    /**
     * the real AfterRequestProcessAdvice.
     * 
     * @return advice class
     */
    Class<? extends AfterRequestProcessAdvice>[] adviceClass() default AfterRequestProcessAdvice.class;

}
