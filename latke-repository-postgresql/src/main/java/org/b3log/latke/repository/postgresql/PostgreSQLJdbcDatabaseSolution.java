package org.b3log.latke.repository.postgresql;/*
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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.repository.jdbc.AbstractJdbcDatabaseSolution;
import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.Connections;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;
import org.b3log.latke.repository.jdbc.util.RepositoryDefinition;
import org.b3log.latke.repository.postgresql.mapping.BooleanMapping;
import org.b3log.latke.repository.postgresql.mapping.DateMapping;
import org.b3log.latke.repository.postgresql.mapping.IntMapping;
import org.b3log.latke.repository.postgresql.mapping.JsonMapping;
import org.b3log.latke.repository.postgresql.mapping.JsonbMapping;
import org.b3log.latke.repository.postgresql.mapping.LongMapping;
import org.b3log.latke.repository.postgresql.mapping.NumberMapping;
import org.b3log.latke.repository.postgresql.mapping.StringMapping;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL database solution
 *
 * @author <a href="https://ld246.com/member/Gakkiyomi2019">Gakkiyomi</a>
 * @version 1.0.0.0, Feb 2, 2021
 */
public class PostgreSQLJdbcDatabaseSolution extends AbstractJdbcDatabaseSolution {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PostgreSQLJdbcDatabaseSolution.class);

    /**
     * Public constructor.
     */
    public PostgreSQLJdbcDatabaseSolution() {
        registerType("int", new IntMapping());
        registerType("boolean", new BooleanMapping());
        registerType("long", new LongMapping());
        registerType("double", new NumberMapping());
        registerType("String", new StringMapping());
        registerType("jsonb", new JsonbMapping());
        registerType("json", new JsonMapping());
        registerType("date", new DateMapping());
    }

    @Override
    public boolean existTable(final String tableName) {
        try (final Connection connection = Connections.getConnection();
             final Statement statement = connection.createStatement()) {
            try {
                statement.execute("SELECT 1 FROM \"" + tableName + "\" LIMIT 1");
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
        sqlBuilder.append(selectSql).append(" FROM ").append("\"").append(tableName).append("\"");
        if (StringUtils.isNotBlank(filterSql)) {
            sqlBuilder.append(" WHERE ").append(filterSql);
        }
        sqlBuilder.append(orderBySql);
        sqlBuilder.append(" LIMIT ").append(end - start).append(" OFFSET ").append(start).append(";");
        return sqlBuilder.toString();
    }

    @Override
    public String getRandomlySql(final String tableName, final int fetchSize) {
        return "SELECT * FROM \"" + tableName + "\" ORDER BY RANDOM() LIMIT " + fetchSize;
    }

    @Override
    protected void createTableHead(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        createTableSqlBuilder.append("CREATE TABLE IF NOT EXISTS ").append("\"").append(repositoryDefinition.getName()).append("\"").append("(");
    }

    @Override
    protected void createTableBody(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        final List<FieldDefinition> keyDefinitionList = new ArrayList<>();
        final List<FieldDefinition> fieldDefinitions = repositoryDefinition.getKeys();
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            final String type = fieldDefinition.getType();
            if (type == null) {
                throw new RuntimeException("The type of fieldDefinitions should not be null");
            }
            final Mapping mapping = getJdbcTypeMapping().get(type);
            if (null != mapping) {
                createTableSqlBuilder.append(mapping.toDataBaseString(fieldDefinition));
                createTableSqlBuilder.append(", ");
                if (fieldDefinition.getIsKey()) {
                    keyDefinitionList.add(fieldDefinition);
                }
            } else {
                throw new RuntimeException("The type [" + fieldDefinition.getType() + "] is not register for mapping");
            }
        }

        createSoftDeleteField(createTableSqlBuilder);
        createTableSqlBuilder.append(createKeyDefinition(keyDefinitionList));
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
            sqlBuilder.append("\"").append(fieldDefinition.getName()).append("\"");
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    protected void createTableEnd(final StringBuilder createTableSqlBuilder, final RepositoryDefinition repositoryDefinition) {
        createTableSqlBuilder.append(");");

        final String description = repositoryDefinition.getDescription();
        if (StringUtils.isNotBlank(description)) {
            createTableSqlBuilder.append("\ncomment on TABLE ").append("\"").append(repositoryDefinition.getName()).append("\"").append(" is ").append("'").append(description).append("';");
        }
        final List<FieldDefinition> keys = repositoryDefinition.getKeys();
        if (null != keys && !keys.isEmpty()) {
            for (FieldDefinition key : keys) {
                if (StringUtils.isNotBlank(key.getDescription()))
                    createTableSqlBuilder.append("\ncomment on COLUMN ").append("\"").append(repositoryDefinition.getName()).append("\"").append(".").append("\"").append(key.getName()).append("\"").append(" is ").append("'").append(key.getDescription()).append("';");
            }
        }
    }
}
