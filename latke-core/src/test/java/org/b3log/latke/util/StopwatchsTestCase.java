/*
 * Copyright (c) 2009-present, b3log.org
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

import junit.framework.Assert;
import org.testng.annotations.Test;

/**
 * {@link Stopwatchs} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.2, Dec 20, 2014
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
     *
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

        final String output = Stopwatchs.getTimingStat();
        System.out.println(output);

        //[100]%, [80]ms [task 1]
        //  [62.5]%, [50]ms [task 1.1]
        //  [25]%, [20]ms [task 1.2]
        //    [0]%, [0]ms [task 1.2.1]
        //      [0]%, [0]ms [task 1.2.1.1]
        //        [0]%, [0]ms [task 1.2.1.1.1]
        //          [0]%, [0]ms [task 1.2.1.1.1.1]
        //            [0]%, [0]ms [task 1.2.1.1.1.1.1]
        //    [25]%, [20]ms [task 1.2.2]
        //  [12.5]%, [10]ms [task 1.3]
        final String[] lines = output.split(Strings.LINE_SEPARATOR);
        Assert.assertEquals(10, lines.length);
    }

    /**
     * Tests method {@link Stopwatchs#getTimingStat()}.
     *
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
            final String output = Stopwatchs.getTimingStat();
            System.out.println(output);

            //[100]%, [-1419045335270]ms [task 1]
            //  [0]%, [50]ms [task 1.1]
            //  [100]%, [-1419045335320]ms [task 1.2]
            //    [0]%, [0]ms [task 1.2.1]
            //    [0]%, [20]ms [task 1.2.2]
            //    [100]%, [-1419045335340]ms [task 1.3]     
            final String[] lines = output.split(Strings.LINE_SEPARATOR);
            Assert.assertEquals(6, lines.length);
        }
    }
}
