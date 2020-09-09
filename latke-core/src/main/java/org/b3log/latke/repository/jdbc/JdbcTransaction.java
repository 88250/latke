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
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, May 10, 2020
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
        try {
            connection.commit();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Commits transaction [" + getId() + "] failed", e);
        } finally {
            dispose();
        }
    }

    @Override
    public void rollback() {
        try {
            connection.rollback();
        } catch (final SQLException e) {
            throw new RuntimeException("Rollbacks transaction [" + getId() + "] failed", e);
        } finally {
            dispose();
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
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
            JdbcRepository.TX.remove();
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
