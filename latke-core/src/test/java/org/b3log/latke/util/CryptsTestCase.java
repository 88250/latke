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

/**
 * {@link Crypts} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jul 13, 2019
 * @since 2.5.1
 */
public class CryptsTestCase {

    @Test
    public void encryptByAES() {
        final String content = "12345678";
        final String key = "12345678";
        final String s = Crypts.encryptByAES(content, key);
        final String content1 = Crypts.decryptByAES(s, key);
        Assert.assertEquals(content, content1);
    }
}
