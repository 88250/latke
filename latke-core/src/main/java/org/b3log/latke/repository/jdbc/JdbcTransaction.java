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
package org.b3log.latke.repository.jdbc;

import org.b3log.latke.logging.Level;
import org.b3log.latke.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.util.Connections;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * JdbcTransaction.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Oct 31, 2018
 */
public final class JdbcTransaction implements Transaction {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(JdbcTransaction.class);

    /**
     * Connection.
     */
    private Connection connection;

    /**
     * Is active.
     */
    private boolean isActive;

    /**
     * Is programmatic.
     */
    private boolean isProgrammatic;

    /**
     * Public constructor.
     *
     * @throws SQLException SQLException
     */
    public JdbcTransaction() throws SQLException {
        connection = Connections.getConnection();
        connection.setAutoCommit(false);
        isActive = true;
    }

    @Override
    public void commit() {
        boolean succ = false;
        try {
            connection.commit();
            succ = true;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Commits transaction [" + getId() + "] failed", e);
        }

        if (succ) {
            dispose();
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
        } catch (final SQLException e) {
            throw new RuntimeException("rollback mistake", e);
        } finally {
            dispose();
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    /**
     * Determines whether this transaction is programmatic.
     *
     * @return {@code true} if this transaction is programmatic, returns {@code false} otherwise
     */
    public boolean isProgrammatic() {
        return isProgrammatic;
    }

    /**
     * Sets this transaction is programmatic with the specified flag.
     *
     * @param isProgrammatic the specified flag
     */
    public void setProgrammatic(final boolean isProgrammatic) {
        this.isProgrammatic = isProgrammatic;
    }

    /**
     * Disposes this transaction.
     */
    public void dispose() {
        try {
            connection.close();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Disposes transaction [" + getId() + "] failed", e);
        } finally {
            isActive = false;
            connection = null;
            JdbcRepository.TX.set(null);
        }
    }

    /**
     * Gets the underlying connection of this transaction.
     *
     * @return {@link Connection}
     */
    public Connection getConnection() {
        return connection;
    }
}
