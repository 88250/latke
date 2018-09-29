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
package org.b3log.latke.ioc.drink;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.config.Configurator;
import org.b3log.latke.ioc.drink.annotation.OddLiteral;
import org.b3log.latke.ioc.drink.juice.Juice;
import org.b3log.latke.ioc.drink.juice.JuiceBottle;
import org.b3log.latke.ioc.drink.mix.Mix;
import org.b3log.latke.ioc.drink.mix.MixBottle;
import org.b3log.latke.ioc.drink.wine.Spirit;
import org.b3log.latke.ioc.drink.wine.WineBottle;
import org.b3log.latke.ioc.inject.Singleton;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 29, 2018
 */
final public class BottleUnitTest {

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    /**
     * Classes package.
     */
    public static final List<Class<?>> drinkPackageClasses =
            Arrays.asList(Juice.class,
                    Spirit.class,
                    Mix.class,
                    WineBottle.class,
                    JuiceBottle.class,
                    MixBottle.class);

    /**
     * Wine bottle.
     */
    private static WineBottle wineBottle;

    /**
     * Juice bottle.
     */
    private static JuiceBottle juiceBottle;

    /**
     * Mix bottle.
     */
    private static MixBottle mixBottle;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() {
        System.out.println("before BottleUnitTest");

        Latkes.initRuntimeEnv();
        Lifecycle.startApplication(drinkPackageClasses);

        beanManager = BeanManager.getInstance();

        // Creates bean by APIs
        final Configurator configurator = beanManager.getConfigurator();
        configurator.createBean(Mix.class).named("spiritMix").
                scoped(Singleton.class).
                qualified(new OddLiteral());

        configurator.validate(); // Validate after bean configuration

        final Bean<?> wineBottleBean = beanManager.getBean(WineBottle.class);
        assertNotNull(wineBottleBean);
        wineBottle = (WineBottle) beanManager.getReference(wineBottleBean);
        assertNotNull(wineBottle);

        final Bean<?> juiceBottleBean = beanManager.getBean(JuiceBottle.class);
        assertNotNull(juiceBottleBean);
        juiceBottle = (JuiceBottle) beanManager.getReference(juiceBottleBean);
        assertNotNull(juiceBottle);

        final Bean<?> mixBottleBean = beanManager.getBean(MixBottle.class, new HashSet<Annotation>());
        mixBottle = (MixBottle) beanManager.getReference(mixBottleBean);
        assertNotNull(mixBottle);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest BottleUnitTest");

        Lifecycle.endApplication();
    }

    @Test
    public void wineBottlePour() {
        System.out.println("wineBottlePour");

        final Spirit wine = wineBottle.pour();
        assertNotNull(wine);
    }

    @Test
    public void juiceBottlePour() {
        System.out.println("juiceBottlePour");

        final Juice juice = juiceBottle.pour();
        assertNotNull(juice);
    }

    @Test
    public void mixBottlePour() {
        System.out.println("mixBottlePour");

        final Juice juice = mixBottle.pour();
        assertNotNull(juice);
    }
}
