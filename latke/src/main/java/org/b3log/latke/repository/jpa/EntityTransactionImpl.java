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
package org.b3log.latke.repository.jpa;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import org.b3log.latke.repository.Transaction;

/**
 * Entity transaction implementation.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 11, 2011
 */
public final class EntityTransactionImpl implements EntityTransaction {

    /**
     * Transaction.
     */
    private Transaction transaction;

    /**
     * Constructs a entity transaction with the specified transaction.
     * 
     * @param transaction the specified transaction
     */
    public EntityTransactionImpl(final Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void begin() {
        if (transaction.isActive()) {
            throw new IllegalStateException("This transaction is active");
        }
    }

    @Override
    public void commit() {
        if (!transaction.isActive()) {
            throw new IllegalStateException("This transaction is inactive");
        }

        try {
            transaction.commit();
        } catch (final Exception e) {
            throw new RollbackException("Commit failed", e);
        }
    }

    @Override
    public void rollback() {
        if (!transaction.isActive()) {
            throw new IllegalStateException("This transaction is inactive");
        }

        try {
            transaction.rollback();
        } catch (final Exception e) {
            throw new PersistenceException("Rollback failed", e);
        }
    }

    @Override
    public void setRollbackOnly() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getRollbackOnly() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isActive() {
        return transaction.isActive();
    }
}
