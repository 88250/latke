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
package org.b3log.latke.model;


/**
 * This class defines all pagination model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Sep 2, 2013
 */
public final class Pagination {

    /**
     * Pagination.
     */
    public static final String PAGINATION = "pagination";

    /**
     * Pagination page count.
     */
    public static final String PAGINATION_PAGE_COUNT = "paginationPageCount";

    /**
     * Pagination record count.
     */
    public static final String PAGINATION_RECORD_COUNT = "paginationRecordCount";

    /**
     * Pagination current page number.
     */
    public static final String PAGINATION_CURRENT_PAGE_NUM = "paginationCurrentPageNum";

    /**
     * Pagination page size.
     */
    public static final String PAGINATION_PAGE_SIZE = "paginationPageSize";

    /**
     * Pagination window size.
     */
    public static final String PAGINATION_WINDOW_SIZE = "paginationWindowSize";

    /**
     * Pagination page numbers.
     */
    public static final String PAGINATION_PAGE_NUMS = "paginationPageNums";

    /**
     * Pagination first page number.
     */
    public static final String PAGINATION_FIRST_PAGE_NUM = "paginationFirstPageNum";

    /**
     * Pagination last page number.
     */
    public static final String PAGINATION_LAST_PAGE_NUM = "paginationLastPageNum";

    /**
     * Key of previous page number.
     */
    public static final String PAGINATION_PREVIOUS_PAGE_NUM = "paginationPreviousPageNum";

    /**
     * Key of next page number.
     */
    public static final String PAGINATION_NEXT_PAGE_NUM = "paginationNextPageNum";

    /**
     * Private constructor.
     */
    private Pagination() {
    }
}
