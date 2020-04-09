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

/**
 * Filter operator.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.2, Jul 15, 2019
 * @see Filter
 */
public enum FilterOperator {

    /**
     * Less than.
     */
    LESS_THAN,
    /**
     * Less than or equal.
     */
    LESS_THAN_OR_EQUAL,
    /**
     * Greater than.
     */
    GREATER_THAN,
    /**
     * Grater than or equal.
     */
    GREATER_THAN_OR_EQUAL,
    /**
     * Equal.
     */
    EQUAL,
    /**
     * Not equal.
     */
    NOT_EQUAL,
    /**
     * In.
     */
    IN,
    /**
     * Not in.
     */
    NOT_IN,
    /**
     * Like.
     *
     * <p>
     * <b>Note</b>: This operation just support JDBC repository.
     * </p>
     */
    LIKE,
    /**
     * Not like.
     *
     * <p>
     * <b>Note</b>: This operation just support JDBC repository.
     * </p>
     */
    NOT_LIKE,
}
