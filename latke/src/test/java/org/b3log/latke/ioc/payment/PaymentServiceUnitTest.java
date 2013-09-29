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
package org.b3log.latke.ioc.payment;

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
import org.b3log.latke.ioc.payment.annotation.PayLiteral;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.testng.annotations.AfterTest;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.0, Nov 14, 2009
 */
final public class PaymentServiceUnitTest {

    /**
     * Bean manager.
     */
    private LatkeBeanManager beanManager;

    public static final List<Class<?>> paymentPackageClasses =
            Arrays.<Class<?>>asList(AsynchronousPaymentProcessor.class,
            PaymentService.class,
            Printer.class,
            SynchronousPaymentProcessor.class,
            UserService.class);

    private static PaymentService paymentService;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() throws Exception {
        System.out.println("before PaymentServiceUnitTest");

        Latkes.initRuntimeEnv();
        Lifecycle.startApplication(paymentPackageClasses);
        
        beanManager = LatkeBeanManagerImpl.getInstance();

        final Set<Annotation> paymentServiceQualifiers = new HashSet<Annotation>();
        paymentServiceQualifiers.add(new PayLiteral());
        final LatkeBean<?> bean = beanManager.getBean(PaymentService.class, paymentServiceQualifiers);
        paymentService = (PaymentService) beanManager.getReference(bean);

        assertNotNull(paymentService);

        // get a PaymentService reference again to check whether it is the same instance or not
        final PaymentService paySvc = (PaymentService) beanManager.getReference(bean);
        assertEquals(paySvc, paymentService);
    }

    /**
     * This method will be run after the test. Shutdown Latke IoC container.
     */
    @AfterTest
    public void afterTest() {
        System.out.println("afterTest PaymentServiceUnitTest");
        Lifecycle.endApplication();
    }

    /**
     * Test field injection.
     */
    @Test
    public void getUserService() {
        System.out.println("getUserService");

        final UserService userService = paymentService.getUserService();
        assertNotNull(userService);
        assertEquals(userService.getAdminName(), "Admin");

        // check whether the references are the same
        final LatkeBean<?> userSvcBean = beanManager.getBean("userService");
        final UserService userSvc = (UserService) beanManager.getReference(userSvcBean);
        assertEquals(userService, userSvc);
    }

    /**
     * Tests field injection.
     */
    @Test
    public void pay() {
        System.out.println("pay");
        paymentService.pay();
    }

    /**
     * Tests method injection.
     */
    @Test
    public void getSessionScopedSynProcessor() {
        System.out.println("getSessionScopedSynProcessor");
        final PaymentProcessor sessionScopedSynProcessor = paymentService.getSessionScopedSynProcessor();
        assertNotNull(sessionScopedSynProcessor);

        sessionScopedSynProcessor.process();
    }

    /**
     * Tests provider injection.
     */
    @Test
    public void getAsynProcessor() {
        System.out.println("getAsynProcessor");
        final PaymentProcessor asynProcessor = paymentService.getAsynProcessor();
        assertNotNull(asynProcessor);
        asynProcessor.process();
    }
}
