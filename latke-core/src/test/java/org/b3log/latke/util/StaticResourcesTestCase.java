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
package org.b3log.latke.util;

import org.b3log.latke.Latkes;
import org.b3log.latke.servlet.mock.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link StaticResources} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Mar 31, 2012
 */
public class StaticResourcesTestCase {

    static {
        Latkes.init();
    }

    /**
     * Tests method {@link StaticResources#isStatic(HttpServletRequest)}.
     */
    @Test
    public void isStatic() {
        HttpServletRequest request = new MockHttpServletRequest();

        ((MockHttpServletRequest) request).setRequestURI("/css/test.css");
        Assert.assertTrue(StaticResources.isStatic(request));

        request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/images/test.jpg");
        Assert.assertTrue(StaticResources.isStatic(request));

        request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/js/lib/jquery/jquery.min.js");
        Assert.assertTrue(StaticResources.isStatic(request));

        request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/test.notExist");
        Assert.assertFalse(StaticResources.isStatic(request));

        request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).setRequestURI("/images/test");
        Assert.assertFalse(StaticResources.isStatic(request));
    }
}
