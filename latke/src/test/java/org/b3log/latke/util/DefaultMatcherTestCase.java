/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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
package org.b3log.latke.util;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.weborganic.furi.URIResolveResult;

/**
 * {@link AntPathMatcher} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Sep 27, 2012
 */
public final class DefaultMatcherTestCase {

    @Test
    public void matchError() {
        final URIResolveResult result = DefaultMatcher.match("/*.html", "/a.html");

        Assert.assertEquals(result.getStatus(), URIResolveResult.Status.ERROR);
    }

    @Test
    public void match() {
        final URIResolveResult result = DefaultMatcher.match("/{name}.html", "/a.html");

        Assert.assertEquals(result.getStatus(), URIResolveResult.Status.RESOLVED);
    }
}
