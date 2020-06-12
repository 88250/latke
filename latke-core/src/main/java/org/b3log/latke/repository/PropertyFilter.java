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
package org.b3log.latke.repository;

import java.util.Objects;

/**
 * A {@link Filter filter } on a single property.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Oct 21, 2018
 */
public final class PropertyFilter implements Filter {

    /**
     * Key.
     */
    private final String key;

    /**
     * Operator.
     */
    private final FilterOperator operator;

    /**
     * Value.
     */
    private final Object value;

    /**
     * Constructor with the specified parameters.
     *
     * @param key      the specified key
     * @param operator the specified operator
     * @param value    the specified value
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyFilter that = (PropertyFilter) o;
        return Objects.equals(key, that.key) &&
                operator == that.operator &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, operator, value);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("key=");
        stringBuilder.append(key).append(", operator=").append(operator.name()).append(", value=").append(value.toString());
        return stringBuilder.toString();
    }
}
