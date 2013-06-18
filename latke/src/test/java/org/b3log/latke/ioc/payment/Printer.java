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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.b3log.latke.ioc.payment.annotation.Console;
import org.b3log.latke.ioc.payment.annotation.Pay;

/**
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 21, 2009
 */
@Named("consolePrinter")
@ApplicationScoped
@Console
public class Printer {

    private final PaymentService paymentService;

    @Inject
    public Printer(final @Pay PaymentService paymentService) {
        this.paymentService = paymentService;
        System.out.println("injected paymentService in constructor....");
    }

    public void print() {
        System.out.println("Printing....");
        System.out.println("Sum: " + paymentService.getSum());
    }
}
