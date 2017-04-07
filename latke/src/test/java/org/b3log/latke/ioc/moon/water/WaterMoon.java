/*
 * Copyright (c) 2009-2016, b3log.org & hacpai.com
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
package org.b3log.latke.ioc.moon.water;

import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.moon.annotation.Artifical;
import org.b3log.latke.ioc.moon.annotation.Water;
import org.b3log.latke.ioc.moon.ArtificalMoon;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Nov 9, 2009
 */
@Named("waterMoon")
@Artifical
@Water
public class WaterMoon extends ArtificalMoon {

    String description = "default description of artifical water moon";
    public int weight;

    @Override
    @Inject
    public void init() {
        System.out.println("In WaterMoon init method");
    }

    @Inject
    void initDescription() {
        System.out.println("In WaterMoon initDes method");
        description = "artifical";
    }

    @Inject
    void initWeight() {
        System.out.println("In WaterMoon initWeight method");
        weight = -1;
    }
}
