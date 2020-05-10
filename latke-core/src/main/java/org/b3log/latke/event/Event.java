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


/**
 * Event.
 *
 * @param <T> the type of the event data
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Aug 16, 2010
 */
public final class Event<T> {

    /**
     * Type of this event.
     */
    private final String type;

    /**
     * Data of this event.
     */
    private final T data;

    /**
     * Constructs a {@link Event} object with the specified type and data.
     *
     * @param type the specified type
     * @param data the specified data
     */
    public Event(final String type, final T data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Gets the type of this event.
     *
     * @return the type of this event
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the data of this event.
     *
     * @return the data of this event
     */
    public T getData() {
        return data;
    }
}
