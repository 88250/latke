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

import java.util.List;
import java.util.Objects;

/**
 * Composite filter that combines serval sub filters using a {@link CompositeFilterOperator}.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Oct 21, 2018
 * @see CompositeFilterOperator
 */
public final class CompositeFilter implements Filter {

    /**
     * Operator.
     */
    private final CompositeFilterOperator operator;

    /**
     * Sub filters.
     */
    private final List<Filter> subFilters;

    /**
     * Constructor with the specified parameters.
     *
     * @param operator   the specified operator
     * @param subFilters the specified sub filters
     */
    public CompositeFilter(final CompositeFilterOperator operator, final List<Filter> subFilters) {
        this.operator = operator;
        this.subFilters = subFilters;
    }

    /**
     * Gets the sub filters.
     *
     * @return sub filters
     */
    public List<Filter> getSubFilters() {
        return subFilters;
    }

    /**
     * Gets the operator.
     *
     * @return operator
     */
    public CompositeFilterOperator getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositeFilter that = (CompositeFilter) o;
        return operator == that.operator && Objects.equals(subFilters, that.subFilters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, subFilters);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("operator=");
        stringBuilder.append(operator).append(", filters=[");
        for (int i = 0; i < subFilters.size(); i++) {
            final Filter filter = subFilters.get(i);
            stringBuilder.append("filter=[").append(filter.toString()).append("]");
            if (i < subFilters.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
