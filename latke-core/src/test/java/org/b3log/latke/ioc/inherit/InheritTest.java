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
package org.b3log.latke.ioc.inherit;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Discoverer;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.assertNotNull;

public class InheritTest {

    private BeanManager beanManager;

    private Interface anInterface;
    private C0 c0;

    @BeforeTest
    public void beforeTest() throws Exception {
        System.out.println("before " + InheritTest.class.getSimpleName());

        Latkes.init();
        beanManager = BeanManager.getInstance();

        final Collection<Class<?>> beanClasses = Discoverer.discover("org.b3log.latke.ioc.inherit");
        BeanManager.start(beanClasses);

        anInterface = beanManager.getReference(InterfaceImpl.class);
        c0 = beanManager.getReference(C0.class);
        assertNotNull(anInterface);
    }

    @AfterTest
    public void afterTest() {
        System.out.println("afterTest " + InheritTest.class.getSimpleName());
        BeanManager.close();
    }

    @Test
    public void hello() {
        System.out.println("hello");
        anInterface.hello();

//        assertTrue(c0.injected());
    }
}
