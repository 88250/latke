/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * LianDi is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.repository.jdbc;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOGGER = LogManager.getLogger(JdbcTransaction.class);

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
