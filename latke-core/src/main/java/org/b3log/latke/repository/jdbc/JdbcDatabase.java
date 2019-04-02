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

import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.SQLException;

/**
 * interface JdbcDatabase.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
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
