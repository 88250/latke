/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * Latke is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.event;

import org.json.JSONObject;
import org.testng.annotations.Test;

/**
 * {@link EventManager} test case.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.6, Oct 31, 2018
 */
public final class EventManagerTestCase {

    @Test
    public void test() {
        final EventManager eventManager = new EventManager();
        final TestEventListener1 testEventListener1 = new TestEventListener1();
        eventManager.registerListener(testEventListener1);
        final TestEventListener2 testEventListener2 = new TestEventListener2();
        eventManager.registerListener(testEventListener2);
        final TestEventAsyncListener1 testEventAsyncListener1 = new TestEventAsyncListener1();
        eventManager.registerListener(testEventAsyncListener1);

        final JSONObject eventData = new JSONObject();
        eventData.put("prop1", 1);

        eventManager.fireEventSynchronously(new Event<JSONObject>("Test sync listener1", eventData));
        eventManager.fireEventSynchronously(new Event<JSONObject>("Test sync listener2", eventData));

        eventManager.<String>fireEventAsynchronously(new Event<JSONObject>("Test async listener1", eventData));
        System.out.println("Doing somthing in simplest thread....");
        final long sleepTime = 51;
        final long loopCnt = 20;
        try {
            for (int i = 0; i < loopCnt; i++) {
                System.out.println("In simplest thread: " + i);
                Thread.sleep(sleepTime);
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done in simplest thread");
    }

    /**
     * Test event listener 1.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.1, Aug 27, 2010
     */
    private final class TestEventListener1 extends AbstractEventListener<JSONObject> {

        @Override
        public void action(final Event<JSONObject> event) {
            System.out.println("Listener1 is processing a event[type=" + event.getType() + ", data=" + event.getData() + "]");
        }

        @Override
        public String getEventType() {
            return "Test sync listener1";
        }
    }

    /**
     * Test event listener 2.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.1, Jun 23, 2011
     */
    private final class TestEventListener2 extends AbstractEventListener<JSONObject> {

        @Override
        public void action(final Event<JSONObject> event) {
            System.out.println("Listener2 is processing a event[type=" + event.getType() + ", data=" + event.getData() + "]");
        }

        @Override
        public String getEventType() {
            return "Test sync listener2";
        }
    }

    /**
     * Test event asynchronous listener 1.
     *
     * @author <a href="http://88250.b3log.org">Liang Ding</a>
     * @version 1.0.0.2, Jun 23, 2011
     */
    private final class TestEventAsyncListener1 extends AbstractEventListener<JSONObject> {

        @Override
        public void action(final Event<JSONObject> event) {
            System.out.println("Asynchonous listener1 is processing a event[type=" + event.getType() + ", data=" + event.getData() + "]");
            final long sleepTime = 50;
            final long loopCnt = 20;
            try {
                for (int i = 0; i < loopCnt; i++) {
                    System.out.println("In listener: " + i);
                    Thread.sleep(sleepTime);
                }
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getEventType() {
            return "Test async listener1";
        }
    }
}
