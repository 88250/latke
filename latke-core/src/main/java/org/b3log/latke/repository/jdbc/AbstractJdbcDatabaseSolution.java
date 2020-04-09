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

import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcUtil;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract JDBC database solution.
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.1, Feb 21, 2019
 */
public abstract class AbstractJdbcDatabaseSolution implements JdbcDatabase {

    /**
     * the map Mapping type to real database type.
     */
    private Map<String, Mapping> jdbcTypeMapping = new HashMap<>();

    /**
     * register type to mapping solution.
     *
     * @param type    type from json
     * @param mapping {@link Mapping}
     */
    public void registerType(final String type, final Mapping mapping) {
        jdbcTypeMapping.put(type, mapping);
    }

    @Override
    public boolean createTable(final RepositoryDefinition repositoryDefinition) throws SQLException {
        final Connection connection = Connections.getConnection();

        try {
            final StringBuilder createTableSql = new StringBuilder();
            createTableHead(createTableSql, repositoryDefinition);
            createTableBody(createTableSql, repositoryDefinition);
            createTableEnd(createTableSql, repositoryDefinition);

            return JdbcUtil.executeSql(createTableSql.toString(), connection, false);
        } catch (final SQLException e) {
            throw e;
        } finally {
            connection.close();
        }
    }

    /**
     * abstract createTableHead for each DB to impl.
     *
     * @param createTableSql       createSql
     * @param repositoryDefinition the specified repository definition
     */
    protected abstract void createTableHead(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition);

    /**
     * abstract createTableBody for each DB to impl.
     *
     * @param createTableSql       createSql
     * @param repositoryDefinition the specified repository definition
     */
    protected abstract void createTableBody(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition);

    /**
     * abstract createTableEnd for each DB to impl.
     *
     * @param createTableSql       createSql
     * @param repositoryDefinition the specified repository definition
     */
    protected abstract void createTableEnd(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition);

    /**
     * @return jdbcTypeMapping
     */
    public Map<String, Mapping> getJdbcTypeMapping() {
        return jdbcTypeMapping;
    }
}
