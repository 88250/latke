/*
 * Copyright (c) 2009-2018, b3log.org & hacpai.com
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
package org.b3log.latke.repository;

import java.util.Objects;

/**
 * Projection.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Oct 21, 2018
 */
public class Projection {

    /**
     * Key.
     */
    private String key;

    /**
     * Value type.
     */
    private Class<?> type;

    /**
     * Gets the key.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value type.
     *
     * @return value type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Constructs a projection with the specified key and value type.
     *
     * @param key  the specified key
     * @param type the specified value type
     */
    public Projection(final String key, final Class<?> type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Projection that = (Projection) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, type);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("key=");

        stringBuilder.append(key).append(", typeClassName=").append(type.getClass().getName());

        return stringBuilder.toString();
    }
}
