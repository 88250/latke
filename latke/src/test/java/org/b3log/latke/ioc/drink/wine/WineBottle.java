/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.ioc.drink.wine;


import org.b3log.latke.ioc.drink.Bottle;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.inject.Provider;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2009
 */
public class WineBottle implements Bottle {

    public final Provider<Spirit> wineProvider;

    @Inject
    public WineBottle(final Provider<Spirit> wineProvider) {
        this.wineProvider = wineProvider;
    }

    @Override
    public Spirit pour() {
        return wineProvider.get();
    }
}
