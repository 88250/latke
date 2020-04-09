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
package org.b3log.latke.ioc.inherit;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class InheritTest {

    private BeanManager beanManager;

    private Interface anInterface;
    private C0 c0;

    @BeforeTest
    public void beforeTest() {
        System.out.println("before " + InheritTest.class.getSimpleName());

        Latkes.setScanPath("org.b3log.latke.ioc.inherit");
        Latkes.init();
        beanManager = BeanManager.getInstance();

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
