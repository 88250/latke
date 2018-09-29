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
package org.b3log.latke.ioc.payment;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Lifecycle;
import org.b3log.latke.ioc.bean.Bean;
import org.b3log.latke.ioc.payment.annotation.PayLiteral;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.0, Nov 14, 2009
 */
final public class PaymentServiceUnitTest {

    /**
     * Bean manager.
     */
    private BeanManager beanManager;

    public static final List<Class<?>> paymentPackageClasses =
            Arrays.asList(AsynchronousPaymentProcessor.class,
                    PaymentService.class,
                    Printer.class,
                    SynchronousPaymentProcessor.class,
                    UserService.class);

    private static PaymentService paymentService;

    @BeforeTest
    @SuppressWarnings("unchecked")
    public void beforeTest() {
        System.out.println("before PaymentServiceUnitTest");

        Latkes.initRuntimeEnv();
        Lifecycle.startApplication(paymentPackageClasses);

        beanManager = BeanManager.getInstance();

        final Set<Annotation> paymentServiceQualifiers = new HashSet<>();
        paymentServiceQualifiers.add(new PayLiteral());
        final Bean<?> bean = beanManager.getBean(PaymentService.class, paymentServiceQualifiers);
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
        final Bean<?> userSvcBean = beanManager.getBean("userService");
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
