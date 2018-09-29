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
package org.b3log.latke.ioc.moon;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.Bean;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, Sep 29, 2018
 * @since 2.4.18
 */
final public class MoonUnitTest {

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    public static final List<Class<?>> moonPackageClasses = Arrays.asList(Moon.class);

    private static Moon moon;

    @BeforeTest
    public void beforeTest() {
        System.out.println("before MoonUnitTest");

        Latkes.initRuntimeEnv();
        beanManager = BeanManager.getInstance();
        Lifecycle.startApplication(moonPackageClasses);

        final Bean<?> moonBean = beanManager.getBean(Moon.class);
        moon = (Moon) beanManager.getReference(moonBean);
        assertNotNull(moon);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest MoonUnitTest");
        Lifecycle.endApplication();
    }
}
