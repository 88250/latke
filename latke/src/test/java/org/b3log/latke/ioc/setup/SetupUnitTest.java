/*
 * Copyright (c) 2009, 2010, 2011, 2012, 2013, B3log Team
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

import org.b3log.latke.ioc.LatkeBean;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.LatkeBeanManagerImpl;
import org.b3log.latke.ioc.config.AbstractModule;
import org.b3log.latke.ioc.Lifecycle;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterTest;

/**
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 17, 2009
 */
final public class SetupUnitTest {

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    private Bean3 bean3;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() throws Exception {
        System.out.println("before SetupUnitTest");

        beanManager = LatkeBeanManagerImpl.getInstance();

        Lifecycle.startApplication(null);

        final AbstractModule setupModule = new SetupModule();
        beanManager.getConfigurator().addModule(setupModule);
        final LatkeBean<?> bean = beanManager.getBean("bean3");
        bean3 = (Bean3) beanManager.getReference(bean);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest SetupUnitTest");
        Lifecycle.endApplication();
    }

    @Test
    public void bean3Say() {
        System.out.println("bean3Say");
        assertEquals(bean3.say(), "Bean3");
        assertEquals(bean3.bean1.say(), "Bean1");
        assertEquals(bean3.bean2.say(), "Bean2");
    }
}
