/*
 * Copyright (c) 2015, b3log.org
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
package org.b3log.latke.ioc.payment;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.bean.LatkeBean;
import org.b3log.latke.ioc.LatkeBeanManager;
import org.b3log.latke.ioc.LatkeBeanManagerImpl;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.payment.annotation.ConsoleLiteral;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterTest;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Nov 14, 2009
 */
final public class PrinterUnitTest {

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    private static Printer printer;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() throws Exception {
        System.out.println("before PrinterUnitTest");

        Latkes.initRuntimeEnv();
        beanManager = LatkeBeanManagerImpl.getInstance();

        Lifecycle.startApplication(PaymentServiceUnitTest.paymentPackageClasses);

        final Set<Annotation> printerQualifiers = new HashSet<Annotation>();
        printerQualifiers.add(new ConsoleLiteral());
        final LatkeBean<?> bean = beanManager.getBean(Printer.class, printerQualifiers);
        printer = (Printer) beanManager.getReference(bean);
        assertNotNull(printer);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest PrinterUnitTest");
        Lifecycle.endApplication();
    }

    @Test
    public void print() {
        System.out.println("print");
        printer.print();
    }
}
