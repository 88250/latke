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

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;

/**
 * Abstract event listener (Observer).
 *
 * @param <T> the type of event data
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.6, Oct 31, 2018
 */
public abstract class AbstractEventListener<T> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractEventListener.class);

    /**
     * Gets the event type of this listener could handle.
     *
     * @return event type
     */
    public abstract String getEventType();

    /**
     * Performs the listener {@code action} method with the specified event
     * queue and event.
     *
     * @param eventQueue the specified event
     * @param event      the specified event
     * @see #action(org.b3log.latke.event.Event)
     */
    final void performAction(final AbstractEventQueue eventQueue, final Event<?> event) {
        final Event<T> eventObject = (Event<T>) event;

        try {
            action(eventObject);
        } catch (final Exception e) {
            LOGGER.log(Level.WARN, "Event perform failed", e);
        } finally { // remove event from event queue
            if (eventQueue instanceof SynchronizedEventQueue) {
                final SynchronizedEventQueue synchronizedEventQueue = (SynchronizedEventQueue) eventQueue;

                synchronizedEventQueue.removeEvent(eventObject);
            }
        }
    }

    /**
     * Processes the specified event.
     *
     * @param event the specified event
     */
    public abstract void action(final Event<T> event);
}
