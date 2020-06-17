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
    private final SynchronizedEventQueue synchronizedEventQueue = new SynchronizedEventQueue(this);

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
        final FutureTask<T> futureTask = new FutureTask<>(() -> {
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
