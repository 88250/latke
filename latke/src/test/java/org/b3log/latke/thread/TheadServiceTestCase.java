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
package org.b3log.latke.thread;

import java.util.concurrent.Future;
import org.b3log.latke.Latkes;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link TheadService} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Jan 10, 2014
 */
public class TheadServiceTestCase {

    static {
        Latkes.initRuntimeEnv();
    }

    @Test
    public void submit() throws Exception {
        final ThreadService threadService = ThreadServiceFactory.getThreadService();

        final Thread thread = new Thread() {
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

        Future<?> future = threadService.submit(thread, 2000);
        
        Thread.sleep(600);
        
        Assert.assertTrue(future.isDone());

        future = threadService.submit(thread, 100);
        
        Assert.assertFalse(future.isDone());
    }
}
