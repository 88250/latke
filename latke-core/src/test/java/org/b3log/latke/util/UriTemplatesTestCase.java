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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * {@link UriTemplates} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Dec 4, 2018
 * @since 2.4.34
 */
public class UriTemplatesTestCase {

    @Test
    public void resolve() {
        Map<String, String> result = UriTemplates.resolve("/a", "/a");
        Assert.assertNotNull(result);

        result = UriTemplates.resolve("/a/b", "/a/b");
        Assert.assertNotNull(result);

        result = UriTemplates.resolve("/a.html", "/{v1}.html");
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get("v1"), "a");

        result = UriTemplates.resolve("/a/b/c", "/{v1}/{v2}/c");
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get("v1"), "a");
        Assert.assertEquals(result.get("v2"), "b");

        result = UriTemplates.resolve("/tags/%e7%94%9f%e6%b4%bb", "/tags/{tagTitle}");
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get("tagTitle"), "生活");

        result = UriTemplates.resolve("/tags/生活", "/tags/{tagTitle}");
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get("tagTitle"), "生活");
    }
}
