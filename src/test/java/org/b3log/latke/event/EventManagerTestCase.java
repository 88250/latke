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
package org.b3log.latke.event;

import java.util.logging.Logger;
import org.json.JSONObject;
import org.testng.annotations.Test;

/**
 * {@link EventManager} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Sep 26, 2010
 */
public final class EventManagerTestCase {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(EventManagerTestCase.class.getName());

    /**
     *
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final EventManager eventManager = EventManager.getInstance();
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
        System.out.println("Doing somthing in main thread....");
        final long sleepTime = 101;
        final long loopCnt = 40;
        try {
            for (int i = 0; i < loopCnt; i++) {
                System.out.println("In main thread: " + i);
                Thread.sleep(sleepTime);
            }
        } catch (final InterruptedException e) {
            throw new EventException(e);
        }
        System.out.println("Done in main thread");
    }

    /**
     * Test event listener 1.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
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
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
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
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.2, Jun 23, 2011
     */
    private final class TestEventAsyncListener1 extends AbstractEventListener<JSONObject> {

        @Override
        public void action(final Event<JSONObject> event) throws EventException {
            System.out.println("Asynchonous listener1 is processing a event[type=" + event.getType() + ", data=" + event.getData() + "]");
            final long sleepTime = 100;
            final long loopCnt = 40;
            try {
                for (int i = 0; i < loopCnt; i++) {
                    System.out.println("In listener: " + i);
                    Thread.sleep(sleepTime);
                }
            } catch (final InterruptedException e) {
                throw new EventException(e);
            }
        }

        @Override
        public String getEventType() {
            return "Test async listener1";
        }
    }
}
