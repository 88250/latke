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

import java.util.*;
import java.util.Map.Entry;

/**
 * Query.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.2, Jan 15, 2019
 * @see Projection
 * @see Filter
 * @see SortDirection
 */
public final class Query {

    /**
     * Current page number.
     */
    private int currentPageNum = 1;

    /**
     * Page count.
     */
    private Integer pageCount;

    /**
     * Page size.
     */
    private int pageSize = Integer.MAX_VALUE;

    /**
     * Sorts.
     */
    private final Map<String, SortDirection> sorts = new LinkedHashMap<>();

    /**
     * Filter.
     */
    private Filter filter;

    /**
     * SELECT projections.
     */
    private final List<Projection> projections = new ArrayList<>();

    /**
     * Debug flag. https://github.com/b3log/latke/issues/82
     */
    private boolean debug;

    /**
     * Sets the current page number and page size with the specified current page number and the specified page sieze.
     *
     * @param currentPageNum the specified current page number
     * @param pageSize       the specified page size
     * @return the current query object
     */
    public Query setPage(final int currentPageNum, final int pageSize) {
        setCurrentPageNum(currentPageNum);
        setPageSize(pageSize);
        return this;
    }

    /**
     * Checks whether is debug.
     *
     * @return {@code true} if it is, returns {@code false} otherwise
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug flag.
     *
     * @param debug the specified debug flag
     * @return the current query object
     */
    public Query setDebug(final boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * Set SELECT projections.
     *
     * @param propertyName  the specified property name
     * @param propertyNames the specified other property names
     * @return the current query object
     */
    public Query select(final String propertyName, final String... propertyNames) {
        projections.add(new Projection(propertyName));
        if (null != propertyNames && 0 < propertyNames.length) {
            for (final String name : propertyNames) {
                projections.add(new Projection(name));
            }
        }
        return this;
    }

    /**
     * Get the SELECT projections.
     *
     * @return projections
     */
    public List<Projection> getProjections() {
        return Collections.unmodifiableList(projections);
    }

    /**
     * Adds sort for the specified property with the specified direction.
     *
     * @param propertyName  the specified property name to sort
     * @param sortDirection the specified sort
     * @return the current query object
     */
    public Query addSort(final String propertyName, final SortDirection sortDirection) {
        sorts.put(propertyName, sortDirection);
        return this;
    }

    /**
     * Sets the filter with the specified filter.
     *
     * @param filter the specified filter
     * @return the current query object
     */
    public Query setFilter(final Filter filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Gets the filter.
     *
     * @return filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Gets the current page number.
     *
     * <p>
     * <b>Note</b>: The default value of the current page number is @code -1}.
     * </p>
     *
     * @return current page number
     */
    public int getCurrentPageNum() {
        return currentPageNum;
    }

    /**
     * Sets the current page number with the specified current page number.
     *
     * @param currentPageNum the specified current page number
     * @return the current query object
     */
    public Query setCurrentPageNum(final int currentPageNum) {
        this.currentPageNum = currentPageNum;
        return this;
    }

    /**
     * Sets the page size.
     *
     * <p>
     * <b>Note</b>: The default value of the page size {@code -1}.
     * </p>
     *
     * @return page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page size with the specified page size.
     *
     * @param pageSize the specified page size
     * @return the current query object
     */
    public Query setPageSize(final int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Gets the sorts.
     *
     * @return sorts
     */
    public Map<String, SortDirection> getSorts() {
        return Collections.unmodifiableMap(sorts);
    }

    /**
     * Gets the page count.
     *
     * @return page count
     */
    public Integer getPageCount() {
        return pageCount;
    }

    /**
     * Sets the page count with the specified page count.
     *
     * @param pageCount the specified page count
     * @return the current query object
     */
    public Query setPageCount(final int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Query query = (Query) o;
        return currentPageNum == query.currentPageNum &&
                pageSize == query.pageSize &&
                debug == query.debug &&
                Objects.equals(pageCount, query.pageCount) &&
                Objects.equals(sorts, query.sorts) &&
                Objects.equals(filter, query.filter) &&
                Objects.equals(projections, query.projections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPageNum, pageCount, pageSize, sorts, filter, projections, debug);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("currentPageNum=").append(currentPageNum).append(", pageSize=").
                append(pageSize).append(", pageCount=").append(pageCount).append(", sorts=[");
        final Set<Entry<String, SortDirection>> entrySet = sorts.entrySet();
        final Iterator<Entry<String, SortDirection>> sortsIterator = entrySet.iterator();
        while (sortsIterator.hasNext()) {
            final Entry<String, SortDirection> sort = sortsIterator.next();
            stringBuilder.append("[key=").append(sort.getKey()).append(", direction=").append(sort.getValue().name()).append("]");
            if (sortsIterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        if (null != filter) {
            stringBuilder.append(", filter=[").append(filter.toString()).append("]");
        }
        stringBuilder.append(", projections=[");
        final Iterator<Projection> projectionsIterator = projections.iterator();
        while (projectionsIterator.hasNext()) {
            final Projection projection = projectionsIterator.next();
            stringBuilder.append('[').append(projection.toString()).append(']');
            if (projectionsIterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
