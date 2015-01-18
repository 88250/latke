/*
 * Copyright (c) 2015, b3log.org
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
package org.b3log.latke.intercept;

import javax.inject.Named;
import org.b3log.latke.intercept.annotation.BeforeMethod;

@Named
public class B {

    // intercept method
    @BeforeMethod("org.b3log.latke.intercept.A#oneMethod")
    public void c(final String str) {
        System.out.println("In B#c(" + str + ')');
    }
}
