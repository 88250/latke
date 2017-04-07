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
package org.b3log.latke.ioc.payment;

import org.b3log.latke.ioc.context.ApplicationScoped;
import org.b3log.latke.ioc.inject.Inject;
import org.b3log.latke.ioc.inject.Named;
import org.b3log.latke.ioc.inject.Provider;
import org.b3log.latke.ioc.payment.annotation.Asynchronous;
import org.b3log.latke.ioc.payment.annotation.Pay;
import org.b3log.latke.ioc.payment.annotation.Synchronous;

/**
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.7, Sep 20, 2009
 */
@Named("paymentService")
@ApplicationScoped
@Pay
public class PaymentService {

    // initialize by method injection
    private PaymentProcessor sessionScopedSynProcessor;

    // notice the following field has no qualifier
    @Inject
    private UserService userService;

    @Inject
    @Asynchronous
    private PaymentProcessor applicationScopedAsynProcessor;

    @Inject
    private Provider<AsynchronousPaymentProcessor> asynProcessorProvider;

    public void pay() {
        applicationScopedAsynProcessor.process();
    }

    public UserService getUserService() {
        return userService;
    }

    public PaymentProcessor getAsynProcessor() {
        return asynProcessorProvider.get();
    }

    public int getSum() {
        return 1000;
    }

    @Inject
    public void initSessionScopedSynProcessor(final @Synchronous PaymentProcessor sessionScopedSynProcessor) {
        this.sessionScopedSynProcessor = sessionScopedSynProcessor;
    }

    public PaymentProcessor getSessionScopedSynProcessor() {
        return sessionScopedSynProcessor;
    }

    public void setSessionScopedSynProcessor(final PaymentProcessor sessionScopedSynProcessor) {
        this.sessionScopedSynProcessor = sessionScopedSynProcessor;
    }
}
