/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
