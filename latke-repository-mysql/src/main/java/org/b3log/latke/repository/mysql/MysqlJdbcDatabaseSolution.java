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
package org.b3log.latke.repository.mysql;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.repository.jdbc.AbstractJdbcDatabaseSolution;
import org.b3log.latke.repository.jdbc.mapping.*;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL database solution
 *
 * @author <a href="https://hacpai.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.0.3, May 13, 2020
 */
public class MysqlJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MysqlJdbcDatabaseSolution.class);

    /**
     * Public constructor.
     */
    public MysqlJdbcDatabaseSolution() {
        registerType("int", new IntMapping());
        registerType("boolean", new BooleanMapping());
        registerType("long", new LongMapping());
        registerType("double", new NumberMapping());
        registerType("String", new StringMapping());
        registerType("Date", new DateMapping());
    }

    @Override
    public boolean existTable(final String tableName) {
        try (final Connection connection = Connections.getConnection();
             final Statement statement = connection.createStatement()) {
            try {
                statement.execute("SELECT 1 FROM `" + tableName + "` LIMIT 1");
            } catch (final Throwable e) {
                return false;
            }

            return true;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Checks table [" + tableName + "] existence failed, assumed it existing [true]", e);

            return true;
        }
    }

    @Override
    public String queryPage(final int start, final int end, final String selectSql, final String filterSql, final String orderBySql, final String tableName) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(selectSql).append(" FROM ").append(tableName);
        if (StringUtils.isNotBlank(filterSql)) {
            sqlBuilder.append(" WHERE ").append(filterSql);
        }
        sqlBuilder.append(orderBySql);
        sqlBuilder.append(" LIMIT ").append(start).append(",").append(end - start);
        return sqlBuilder.toString();
    }

    @Override
    public String getRandomlySql(final String tableName, final int fetchSize) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ").append(tableName).append(" ORDER BY RAND() LIMIT ").append(fetchSize);
        return sqlBuilder.toString();
    }

    @Override
    protected void createTableHead(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition) {
        createTableSql.append("CREATE TABLE IF NOT EXISTS ").append(repositoryDefinition.getName()).append("(");
    }

    @Override
    protected void createTableBody(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition) {
        final List<FieldDefinition> keyDefinitionList = new ArrayList<>();
        final List<FieldDefinition> fieldDefinitions = repositoryDefinition.getKeys();
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            final String type = fieldDefinition.getType();
            if (type == null) {
                throw new RuntimeException("The type of fieldDefinitions should not be null");
            }
            final Mapping mapping = getJdbcTypeMapping().get(type);
            if (null != mapping) {
                createTableSql.append(mapping.toDataBaseSting(fieldDefinition));
                final String description = fieldDefinition.getDescription();
                if (StringUtils.isNotBlank(description)) {
                    createTableSql.append(" COMMENT '" + description + "'");
                }
                createTableSql.append(", ");
                if (fieldDefinition.getIsKey()) {
                    keyDefinitionList.add(fieldDefinition);
                }
            } else {
                throw new RuntimeException("The type [" + fieldDefinition.getType() + "] is not register for mapping");
            }
        }

        keyDefinitionList.size();
        createTableSql.append(createKeyDefinition(keyDefinitionList));
    }

    /**
     * the keyDefinitionList tableSql.
     *
     * @param keyDefinitionList keyDefinitionList
     * @return createKeyDefinitionsql
     */
    private String createKeyDefinition(final List<FieldDefinition> keyDefinitionList) {
        final StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" PRIMARY KEY");
        boolean isFirst = true;
        for (FieldDefinition fieldDefinition : keyDefinitionList) {
            if (isFirst) {
                sqlBuilder.append("(");
                isFirst = false;
            } else {
                sqlBuilder.append(",");
            }
            sqlBuilder.append(fieldDefinition.getName());
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    protected void createTableEnd(final StringBuilder createTableSql, final RepositoryDefinition repositoryDefinition) {
        createTableSql.append(") ENGINE=InnoDB");

        final String description = repositoryDefinition.getDescription();
        if (StringUtils.isNotBlank(description)) {
            createTableSql.append(" COMMENT='").append(description).append("'");
        }

        final String charset = repositoryDefinition.getCharset();
        if (StringUtils.isNotBlank(charset)) {
            createTableSql.append(" DEFAULT CHARACTER SET ").append(charset);
        } else {
            createTableSql.append(" DEFAULT CHARACTER SET ").append("utf8mb4");
        }
        final String collate = repositoryDefinition.getCollate();
        if (StringUtils.isNotBlank(collate)) {
            createTableSql.append(" COLLATE ").append(collate);
        } else {
            createTableSql.append(" COLLATE ").append("utf8mb4_general_ci");
        }
    }
}
