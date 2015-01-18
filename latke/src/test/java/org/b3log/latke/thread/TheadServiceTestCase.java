/*
 * Copyright (c) 2009-2015, b3log.org
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
package org.b3log.latke.thread;

import java.util.concurrent.Future;
import org.b3log.latke.Latkes;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link TheadService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.0, Jun 20, 2014
 */
public class TheadServiceTestCase {

    static {
        Latkes.initRuntimeEnv();
    }

    @Test
    public void submit() throws Exception {
        final ThreadService threadService = ThreadServiceFactory.getThreadService();

        final Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("start");

                try {
                    Thread.sleep(5);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("end");
            }
        };

        Future<?> future = threadService.submit(thread1, 2000);

        Thread.sleep(1000);

        Assert.assertTrue(future.isDone());

        final Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println("start");

                try {
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("end");
            }
        };

        future = threadService.submit(thread2, 100);

        Assert.assertNull(future);
    }
}
