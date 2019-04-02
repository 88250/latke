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
package org.b3log.latke.servlet;

import org.b3log.latke.servlet.annotation.After;
import org.b3log.latke.servlet.annotation.Before;
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

    @RequestProcessing("/a")
    public void a(final RequestContext context) {
        context.attr("a", "a");
    }

    @RequestProcessing("/a/{id}/{name}")
    public void a1(final RequestContext context) {
        context.attr("id", context.pathVar("id"));
        context.attr("name", context.pathVar("name"));
    }

    @RequestProcessing("/a/before")
    @Before(TestBeforeAdvice.class)
    public void abefore(final RequestContext context) {
        context.attr("abefore", "abefore");
    }

    public void l(final RequestContext context) {
        context.attr("l", "l");
    }

    @Before(TestBeforeAdvice.class)
    @After(TestAfterAdvice.class)
    public void lbefore(final RequestContext context) {
        context.attr("lbefore", "lbefore");
    }
}
