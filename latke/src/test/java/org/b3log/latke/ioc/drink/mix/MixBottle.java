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
package org.b3log.latke.ioc.drink.mix;

import org.b3log.latke.ioc.drink.*;
import org.b3log.latke.ioc.drink.annotation.ErGuoTou;
import org.b3log.latke.ioc.drink.annotation.Odd;
import org.b3log.latke.ioc.drink.annotation.Orange;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Provider;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Nov 26, 2009
 */
public class MixBottle implements Bottle {

    public final Provider<Drink> wineProvider;
    public final Provider<Drink> juiceProvider;
    public final Provider<Mix> mixProvider;

    @Inject
    public MixBottle(final @ErGuoTou Provider<Drink> wineProvider,
                     final @Orange Provider<Drink> juiceProvider,
                     final @Named("spiritMix") @Odd Provider<Mix> mixProvider,
                     final @Named("spiritMix") Provider<Mix> mixProvider2) {
        this.wineProvider = wineProvider;
        this.juiceProvider = juiceProvider;
        this.mixProvider = mixProvider;
        assert mixProvider2 != null;
        assert mixProvider2.get() != null;
    }

    @Override
    public Mix pour() {
        return mixProvider.get();
    }
}
