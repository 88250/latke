/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
