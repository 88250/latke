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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Synchronized event queue.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Sep 16, 2018
 */
final class SynchronizedEventQueue extends AbstractEventQueue {

    /**
     * Synchronized event queue.
     */
    private final Map<String, List<Event<?>>> synchronizedEvents = new HashMap<>();

    /**
     * Event manager.
     */
    private final EventManager eventManager;

    /**
     * Constructs a {@link SynchronizedEventQueue} object with the specified
     * event manager.
     *
     * @param eventManager the specified event manager
     */
    SynchronizedEventQueue(final EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Fires the specified event.
     *
     * @param event the specified event
     */
    synchronized void fireEvent(final Event<?> event) {
        final String eventType = event.getType();
        List<Event<?>> events = synchronizedEvents.computeIfAbsent(eventType, k -> new ArrayList<>());

        events.add(event);
        setChanged();
        notifyListeners(event);
    }

    /**
     * Removes the specified event from this event queue.
     *
     * @param event the specified event
     */
    synchronized void removeEvent(final Event<?> event) {
        synchronizedEvents.get(event.getType()).remove(event);
    }
}
