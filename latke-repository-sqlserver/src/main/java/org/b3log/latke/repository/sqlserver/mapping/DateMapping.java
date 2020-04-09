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
package org.b3log.latke.repository.sqlserver.mapping;


import org.b3log.latke.repository.jdbc.mapping.Mapping;
import org.b3log.latke.repository.jdbc.util.FieldDefinition;


/**
 * Date mapping.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Mar 10, 2014
 */
public class DateMapping implements Mapping {

    @Override
    public String toDataBaseSting(final FieldDefinition definition) {
        final StringBuilder sql = new StringBuilder();

        sql.append(definition.getName());
        sql.append(" date");
        if (!definition.getNullable()) {
            sql.append(" not null");
        }

        return sql.toString();
    }
}
