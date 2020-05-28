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

import java.util.Arrays;
import java.util.List;

/**
 * Composite filter operator.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, May 28, 2020
 * @see CompositeFilter
 */
public enum CompositeFilterOperator {

    /**
     * And.
     */
    AND,
    /**
     * Or.
     */
    OR;

    /**
     * Builds an composite filter with 'AND' all the specified sub filters.
     *
     * @param subFilters the specified sub filters
     * @return composite filter
     */
    public static CompositeFilter and(final Filter... subFilters) {
        return new CompositeFilter(AND, Arrays.asList(subFilters));
    }

    /**
     * Builds an composite filter with 'AND' all the specified sub filters.
     *
     * @param subFilters the specified sub filters
     * @return composite filter
     */
    public static CompositeFilter and(final List<Filter> subFilters) {
        return new CompositeFilter(AND, subFilters);
    }

    /**
     * Builds an composite filter with 'OR' all the specified sub filters.
     *
     * @param subFilters the specified sub filters
     * @return composite filter
     */
    public static CompositeFilter or(final Filter... subFilters) {
        return new CompositeFilter(OR, Arrays.asList(subFilters));
    }

    /**
     * Builds an composite filter with 'OR' all the specified sub filters.
     *
     * @param subFilters the specified sub filters
     * @return composite filter
     */
    public static CompositeFilter or(final List<Filter> subFilters) {
        return new CompositeFilter(OR, subFilters);
    }
}
