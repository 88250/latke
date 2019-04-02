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
package org.b3log.latke.event;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Singleton;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Event manager.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.2.6, Oct 31, 2018
 */
@Singleton
public class EventManager {

    /**
     * Synchronized event queue.
     */
    private SynchronizedEventQueue synchronizedEventQueue = new SynchronizedEventQueue(this);

    /**
     * Fire the specified event synchronously.
     *
     * @param event the specified event
     */
    public void fireEventSynchronously(final Event<?> event) {
        synchronizedEventQueue.fireEvent(event);
    }

    /**
     * Fire the specified event asynchronously.
     *
     * @param <T>   the result type
     * @param event the specified event
     * @return future result
     */
    public <T> Future<T> fireEventAsynchronously(final Event<?> event) {
        final FutureTask<T> futureTask = new FutureTask<T>(() -> {
            synchronizedEventQueue.fireEvent(event);

            return null; // XXX: Our future????
        });

        Latkes.EXECUTOR_SERVICE.execute(futureTask);

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
     * Unregisters the specified event listener.
     *
     * @param eventListener the specified event listener
     */
    public void unregisterListener(final AbstractEventListener<?> eventListener) {
        synchronizedEventQueue.deleteListener(eventListener);
    }
}
