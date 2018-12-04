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
package org.b3log.latke.servlet.mock;

import org.b3log.latke.servlet.RequestContext;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;

/**
 * Request processor for testing.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Dec 4, 2018
 * @since 2.4.34
 */
@RequestProcessor
public class TestRequestProcessor {

    @RequestProcessing(value = "/a")
    public void a(final RequestContext context) {
        System.out.println("a");
    }

    @RequestProcessing(value = "/a/{id}/{name}")
    public void a1(final RequestContext context) {
        System.out.println("a1: " + context.pathVars());
    }

    public void l(final RequestContext context) {
        System.out.println("l: " + context.requestURI());
    }
}
