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
package org.b3log.latke.repository.jdbc.mapping;

import org.b3log.latke.repository.jdbc.util.FieldDefinition;

/**
 * Long type mapping.
 *
 * <p>
 * Maps Java long type to SQL bigint type.
 * </p>
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.0, Feb 29, 2012
 */
public final class LongMapping implements Mapping {

    @Override
    public String toDataBaseString(final FieldDefinition definition) {
        final StringBuilder sqlBuilder = new StringBuilder("`" + definition.getName() + "`").append(" BIGINT");
        if (!definition.getNullable()) {
            sqlBuilder.append(" NOT NULL");
        }
        return sqlBuilder.toString();
    }
}
