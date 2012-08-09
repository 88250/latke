/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.util;

import org.testng.annotations.Test;

/**
 * {@link Stopwatchs} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, May 30, 2012
 */
public final class StopwatchsTestCase {

    /**
     * Test method invocation count.
     */
    private static final int INVOCATION_COUNT = 30;
    /**
     * Thread pool size.
     */
    private static final int THREAD_POOL_SIZE = 5;

    /**
     * Releases the current thread-local variable after each test method.
     */
    @org.testng.annotations.AfterMethod
    public void afterMethod() {
        Stopwatchs.release();
    }

    /**
     * Tests method {@link Stopwatchs#getTimingStat()}.
     * @throws Exception exception
     */
    @Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATION_COUNT)
    public void getTimingStat() throws Exception {
        System.out.println("getTimingStat");
        Stopwatchs.start("task 1");

        Stopwatchs.start("task 1.1");
        final long task11Time = 50;
        Thread.sleep(task11Time);
        Stopwatchs.end(); // Ends 1.1


        Stopwatchs.start("task 1.2");
        Stopwatchs.start("task 1.2.1");
        Stopwatchs.start("task 1.2.1.1");
        Stopwatchs.start("task 1.2.1.1.1");
        Stopwatchs.start("task 1.2.1.1.1.1");
        Stopwatchs.start("task 1.2.1.1.1.1.1");
        Stopwatchs.end(); // Ends 1.2.1.1.1.1.1
        Stopwatchs.end(); // Ends 1.2.1.1.1.1
        Stopwatchs.end(); // Ends 1.2.1.1.1
        Stopwatchs.end(); // Ends 1.2.1.1
        Stopwatchs.end(); // Ends 1.2.1

        Stopwatchs.start("task 1.2.2");
        final long task122Time = 20;
        Thread.sleep(task122Time);
        Stopwatchs.end(); // Ends 1.2.2

        Stopwatchs.end(); // Ends 1.2

        Stopwatchs.start("task 1.3");
        final long task13Time = 10;
        Thread.sleep(task13Time);
        Stopwatchs.end(); // Ends 1.3

        Stopwatchs.end(); // Ends 1

        System.out.println(Stopwatchs.getTimingStat());
    }

    /**
     * Tests method {@link Stopwatchs#getTimingStat()}.
     * @throws Exception exception
     */
    @Test(expectedExceptions = RuntimeException.class)
    public void getTimingStatWhileException() throws Exception {
        System.out.println("getTimingStatWhileException");
        Stopwatchs.start("task 1");

        Stopwatchs.start("task 1.1");
        try {
            throw new RuntimeException();
        } finally {
            final long task11Time = 50;
            Thread.sleep(task11Time);
            Stopwatchs.end(); // Ends 1.1


            Stopwatchs.start("task 1.2");
            Stopwatchs.start("task 1.2.1");
            Stopwatchs.end(); // Ends 1.2.1

            Stopwatchs.start("task 1.2.2");
            final long task122Time = 20;
            Thread.sleep(task122Time);
            Stopwatchs.end(); // Ends 1.2.2

            //Stopwatchs.end(); // Ends 1.2, NOTE

            Stopwatchs.start("task 1.3");
            final long task13Time = 10;
            Thread.sleep(task13Time);

            //Stopwatchs.end(); // Ends 1, NOTE

            System.out.println(Stopwatchs.getTimingStat());
        }
    }
}
