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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Event manager.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jun 23, 2011
 */
public final class EventManager {

    /**
     * Synchronized event queue.
     */
    private SynchronizedEventQueue synchronizedEventQueue = new SynchronizedEventQueue(this);

    /**
     * Fire the specified event synchronously.
     *
     * @param event the specified event
     * @throws EventException event exception
     */
    public void fireEventSynchronously(final Event<?> event) throws EventException {
        synchronizedEventQueue.fireEvent(event);
    }

    /**
     * Fire the specified event asynchronously.
     *
     * @param <T> the result type
     * @param event the specified event
     * @return future result
     * @throws EventException event exception
     */
    public <T> Future<T> fireEventAsynchronously(final Event<?> event) throws EventException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        final FutureTask<T> futureTask = new FutureTask<T>(new Callable<T>() {

            @Override
            public T call() throws Exception {
                synchronizedEventQueue.fireEvent(event);

                return null; // XXX: Our future????
            }
        });

        executorService.execute(futureTask);

        return futureTask;
    }

    /**
     * Registers the specified event listener.
     *
     * @param eventListener the specified event listener
     */
    public void registerListener(final AbstractEventListener<?> eventListener) {
        synchronizedEventQueue.addListener(eventListener);
    }

    /**
     * Gets the {@link EventManager} singleton.
     * 
     * @return a event manager singleton
     */
    public static EventManager getInstance() {
        return EventManagerSingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private EventManager() {
    }

    /**
     * Event manager singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Aug 12, 2010
     */
    private static final class EventManagerSingletonHolder {

        /**
         * Singleton.
         */
        private static final EventManager SINGLETON = new EventManager();

        /**
         * Private default constructor.
         */
        private EventManagerSingletonHolder() {
        }
    }
}
