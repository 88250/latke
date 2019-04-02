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

import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.ioc.Stereotype;

import java.lang.annotation.*;

/**
 * Indicates that an annotated type for HTTP servlet request processing.
 *
 * <p>
 * A request processor is the C (controller) of MVC pattern, which has some methods for requests processing, see {@link RequestProcessing}
 * for more details.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Sep 29, 2018
 * @see RequestProcessing
 * @since 2.4.18
 */
@Singleton
@Stereotype
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestProcessor {
}
