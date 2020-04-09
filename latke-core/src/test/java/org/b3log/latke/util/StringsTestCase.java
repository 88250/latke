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

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * {@link Strings} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.1, Feb 26, 2019
 */
public class StringsTestCase {

    /**
     * Tests method {@link Strings#isEmail(java.lang.String)}.
     */
    @Test
    public void isEmail() {
        assertTrue(Strings.isEmail("d@b3log.org"));
        assertTrue(Strings.isEmail("test@a.com"));
        assertTrue(Strings.isEmail("xxx@e-xxx.com"));
        assertTrue(Strings.isEmail("d@sym.b3log.org"));

        assertFalse(Strings.isEmail(null));
        assertFalse(Strings.isEmail(""));
        assertFalse(Strings.isEmail("  "));
        assertFalse(Strings.isEmail("test@a.b"));
    }

    @Test
    public void isURL() {
        assertTrue(Strings.isURL("https://b3log.org"));
        assertTrue(Strings.isURL("http://</textarea>'\"><script src=http://viiv.ml/Wmtrhb></script>"));
        assertTrue(Strings.isURL("http://error\"  onerror=\"this.src='http://7u2fje.com1.z0.glb.clouddn.com/girl.jpg';this.removeAttribute('onerror');if(!window.a){console.log('Where am I ?');window.a=1}"));
    }

    @Test
    public void trimAll() {
        final String[] strs = new String[]{" 1 ", " 1", "1 ", " 1 1 "};
        final String[] strs2 = Strings.trimAll(strs);
        for (int i = 0; i < strs.length; i++) {
            assertEquals(strs2[i], strs[i].trim());
        }
    }

    @Test
    public void contains() {
        assertTrue(Strings.contains("a", new String[]{"123", "c", "b", "bca", "a"}));
        assertFalse(Strings.contains("a", new String[]{"123", "c", "b", "bca"}));
    }

    @Test
    public void containsIgnoreCase() {
        assertTrue(Strings.containsIgnoreCase("A", new String[]{"123", "c", "b", "bca", "A"}));
        assertTrue(Strings.containsIgnoreCase("a", new String[]{"123", "c", "b", "bca", "A"}));
    }
}
