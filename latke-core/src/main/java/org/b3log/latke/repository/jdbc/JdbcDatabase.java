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

import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.SQLException;

/**
 * interface JdbcDatabase.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 21, 2019
 */
public interface JdbcDatabase {

    /**
     * Checks whether a table specified by the given table name exists.
     *
     * @param tableName the given table name
     * @return {@code true} if it exists, returns {@code false} otherwise
     * @since 2.4.43
     */
    boolean existTable(final String tableName);

    /**
     * Creates table with the specified repository definition.
     *
     * @param repositoryDefinition the specified repository definition
     * @return {@code true} if successfully, returns {@code false} otherwise
     * @throws SQLException SQLException
     */
    boolean createTable(final RepositoryDefinition repositoryDefinition) throws SQLException;

    /**
     * queryPage sql.
     *
     * @param start      start
     * @param end        end
     * @param selectSql  selectSql
     * @param filterSql  filterSql
     * @param orderBySql orderBySql
     * @param tableName  tableName
     * @return sql
     */
    String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql, final String tableName);

    /**
     * getRandomlySql.
     *
     * @param tableName tableName
     * @param fetchSize fetchSize
     * @return sql sql
     */
    String getRandomlySql(final String tableName, final int fetchSize);
}
