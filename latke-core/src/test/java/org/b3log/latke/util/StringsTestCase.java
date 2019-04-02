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
