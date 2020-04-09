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

import java.util.UUID;

/**
 * Transaction.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Oct 31, 2018
 */
public interface Transaction {

    /**
     * Gets the id of this transaction.
     *
     * @return id
     */
    default String getId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Commits this transaction.
     *
     * <p>
     * <b>Throws</b>:<br/>
     * {@link java.lang.IllegalStateException} - if the transaction has already been committed, rolled back
     * </p>
     */
    void commit();

    /**
     * Rolls back this transaction.
     *
     * <p>
     * <b>Throws</b>:<br/>
     * {@link java.lang.IllegalStateException} - if the transaction has already been committed, rolled back
     * </p>
     */
    void rollback();

    /**
     * Determines whether this transaction is active.
     *
     * @return {@code true} if this transaction is active, returns {@code false} otherwise
     */
    boolean isActive();
}
