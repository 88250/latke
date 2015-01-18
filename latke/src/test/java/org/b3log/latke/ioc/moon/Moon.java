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
package org.b3log.latke.ioc.moon;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Nov 9, 2009
 */
@Singleton
public class Moon {

    String name;
    int weight;
    String description = "default description of moon";

    @Inject
    public void init() {
        System.out.println("In Moon init method");
    }

    @Inject
    void initName() {
        System.out.println("In Moon initName method");
        name = "Moon";
    }

    @Inject
    void initDescription() {
        System.out.println("In Moon initDes method");
        description = "real";
    }

    @Inject
    void initWeight() {
        System.out.println("In Moon initWeight method");
        weight = 1;
    }
}
