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
package org.b3log.latke.ioc.setup;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Bean;
import org.b3log.latke.ioc.BeanManager;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 29, 2018
 * @since 2.4.18
 */
public final class SetupUnitTest {

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    private Bean3 bean3;

    @BeforeTest
    public void beforeTest() {
        System.out.println("before SetupUnitTest");

        Latkes.init();

        beanManager = BeanManager.getInstance();

        final Set<Class<?>> beanClasses = new HashSet<>();
        beanClasses.add(Bean1.class);
        beanClasses.add(Bean2.class);
        beanClasses.add(Bean3.class);
        BeanManager.start(beanClasses);

        final Bean<?> bean = beanManager.getBean(Bean3.class);
        bean3 = (Bean3) beanManager.getReference(bean);
        assertNotNull(bean3);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest SetupUnitTest");
        BeanManager.close();
    }

    @Test
    public void bean3Say() {
        System.out.println("bean3Say");
        assertEquals(bean3.say(), "Bean3");
        assertEquals(bean3.bean1.say(), "Bean1");
        assertEquals(bean3.bean2.say(), "Bean2");
    }
}
