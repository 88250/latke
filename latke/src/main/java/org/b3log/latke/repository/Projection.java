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
package org.b3log.latke.repository;

/**
 * Projection.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, May 8, 2012
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
     * Initialization value for hashing.
     */
    private static final int INIT_HASH = 3;
    /**
     * Base for hashing.
     */
    private static final int BASE = 29;

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
     * @param key the specified key
     * @param type the specified value type
     */
    public Projection(final String key, final Class<?> type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Projection other = (Projection) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }

        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int ret = INIT_HASH;

        ret = BASE * ret + (this.key != null ? this.key.hashCode() : 0);
        ret = BASE * ret + (this.type != null ? this.type.hashCode() : 0);

        return ret;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("key=");
        stringBuilder.append(key).append(", typeClassName=").append(type.getClass().getName());

        return stringBuilder.toString();
    }
}
