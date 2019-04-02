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
    private CompositeFilterOperator operator;

    /**
     * Sub filters.
     */
    private List<Filter> subFilters;

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
        return operator == that.operator &&
                Objects.equals(subFilters, that.subFilters);
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
