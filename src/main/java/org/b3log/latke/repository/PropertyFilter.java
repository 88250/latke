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
 * A {@link Filter filter } on a single property.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 2.0.0.0, Jun 27, 2012
 */
public final class PropertyFilter implements Filter {

    /**
     * Key.
     */
    private String key;
    /**
     * Operator.
     */
    private FilterOperator operator;
    /**
     * Value.
     */
    private Object value;
    /**
     * Initialization value for hashing.
     */
    private static final int INIT_HASH = 7;
    /**
     * Base for hashing.
     */
    private static final int BASE = 97;

    /**
     * Constructor with the specified parameters.
     *
     * @param key the specified key
     * @param operator the specified operator
     * @param value the specified value
     */
    public PropertyFilter(final String key, final FilterOperator operator, final Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Gets the key.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the operator.
     *
     * @return operator
     */
    public FilterOperator getOperator() {
        return operator;
    }

    /**
     * Gets the value.
     *
     * @return value
     */
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final PropertyFilter other = (PropertyFilter) obj;
        if ((this.key == null) ? (other.key != null)
            : !this.key.equals(other.key)) {
            return false;
        }

        if (this.operator != other.operator) {
            return false;
        }

        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int ret = INIT_HASH;

        ret = BASE * ret + (this.key != null ? this.key.hashCode() : 0);
        ret = BASE * ret + (this.operator != null ? this.operator.hashCode() : 0);
        ret = BASE * ret + (this.value != null ? this.value.hashCode() : 0);

        return ret;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("key=");
        stringBuilder.append(key).append(", operator=").append(operator.name()).
                append(", value=").append(value.toString());

        return stringBuilder.toString();
    }
}
