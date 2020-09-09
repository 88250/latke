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

import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.JdbcRepositories;
import org.b3log.latke.repository.jdbc.util.JdbcUtil;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract JDBC database solution.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.2, Jun 20, 2020
 */
public abstract class AbstractJdbcDatabaseSolution implements JdbcDatabase {

    /**
     * the map Mapping type to real database type.
     */
    private final Map<String, Mapping> jdbcTypeMapping = new HashMap<>();

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
        try (final Connection connection = Connections.getConnection()) {
            final StringBuilder createTableSqlBuilder = new StringBuilder();
            createTableHead(createTableSqlBuilder, repositoryDefinition);
            createTableBody(createTableSqlBuilder, repositoryDefinition);
            createTableEnd(createTableSqlBuilder, repositoryDefinition);
            return JdbcUtil.executeSql(createTableSqlBuilder.toString(), connection, false);
        }
    }

    /**
     * abstract createTableHead for each DB to impl.
     *
     * @param createTableSqlBuilder createSql
     * @param repositoryDefinition  the specified repository definition
     */
    protected abstract void createTableHead(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition);

    /**
     * abstract createTableBody for each DB to impl.
     *
     * @param createTableSqlBuilder createSql
     * @param repositoryDefinition  the specified repository definition
     */
    protected abstract void createTableBody(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition);

    /**
     * abstract createTableEnd for each DB to impl.
     *
     * @param createTableSqlBuilder createSql
     * @param repositoryDefinition  the specified repository definition
     */
    protected abstract void createTableEnd(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition);

    protected void createSoftDeleteField(final StringBuilder createTableSqlBuilder) {
        if (!Repositories.isSoftDelete()) {
            return;
        }
        createTableSqlBuilder.append(JdbcRepositories.softDeleteFieldName).append(" INT NOT NULL, ");
    }

    /**
     * @return jdbcTypeMapping
     */
    public Map<String, Mapping> getJdbcTypeMapping() {
        return jdbcTypeMapping;
    }
}
