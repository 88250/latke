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
package org.b3log.latke.ioc.moon;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.LatkeBeanManagerImpl;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.literal.NamedLiteral;
import org.b3log.latke.ioc.moon.annotation.ArtificalLiteral;
import org.b3log.latke.ioc.moon.annotation.WaterLiteral;
import org.b3log.latke.ioc.moon.water.WaterMoon;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterTest;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Nov 8, 2009
 */
final public class MoonUnitTest {

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    public static final List<Class<?>> moonPackageClasses = Arrays.<Class<?>>asList(Moon.class, ArtificalMoon.class, WaterMoon.class);

    private static Moon moon;

    private static ArtificalMoon artificalMoon;

    private static WaterMoon waterMoon;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() throws Exception {
        System.out.println("before MoonUnitTest");

        Latkes.initRuntimeEnv();

        beanManager = LatkeBeanManagerImpl.getInstance();

        Lifecycle.startApplication(moonPackageClasses);

        final Set<Annotation> moonQualifiers = new HashSet<Annotation>();
        moonQualifiers.add(new NamedLiteral("moon"));
        final LatkeBean<?> moonBean = beanManager.getBean(Moon.class, moonQualifiers);
        moon = (Moon) beanManager.getReference(moonBean);
        assertNotNull(moon);

        final Set<Annotation> artificalMoonQualifiers = new HashSet<Annotation>();
        artificalMoonQualifiers.add(new ArtificalLiteral());
        final LatkeBean<?> artificalMoonBean = beanManager.getBean(ArtificalMoon.class, artificalMoonQualifiers);
        artificalMoon = (ArtificalMoon) beanManager.getReference(artificalMoonBean);
        assertNotNull(artificalMoon);

        final Set<Annotation> waterMoonQualifiers = new HashSet<Annotation>();
        waterMoonQualifiers.add(new ArtificalLiteral());
        waterMoonQualifiers.add(new WaterLiteral());
        final LatkeBean<?> waterMoonBean = beanManager.getBean(WaterMoon.class, waterMoonQualifiers);
        waterMoon = (WaterMoon) beanManager.getReference(waterMoonBean);
        assertNotNull(waterMoon);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest MoonUnitTest");
        Lifecycle.endApplication();
    }

    @Test
    public void initDescription() {
        System.out.println("initMoonDescription");
        assertEquals(moon.description, "real");
//        assertEquals(artificalMoon.description, "artifical");
        assertEquals(((Moon) artificalMoon).description, "default description of moon");
    }

    //@Test
    public void initWeight() {
        System.out.println("initWeight");
        assertEquals(moon.weight, 1);
        assertEquals(artificalMoon.weight, 0);
        assertEquals(waterMoon.weight, -1);
        assertEquals(((ArtificalMoon) waterMoon).weight, 0);
        assertEquals(((Moon) waterMoon).weight, 0);
    }

    //@Test
    public void initName() {
        System.out.println("initName");
        assertEquals(moon.name, "Moon");
        assertEquals(artificalMoon.name, "Moon");
    }
}
