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
 * String mapping.
 *
 * @author <a href="https://ld246.com/member/mainlove">Love Yao</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jun 20, 2020
 */
public class StringMapping implements Mapping {

    @Override
    public String toDataBaseString(final FieldDefinition definition) {
        final StringBuilder sqlBuilder = new StringBuilder(("`" + definition.getName() + "`"));
        if (null == definition.getLength()) {
            definition.setLength(0);
        }

        final Integer length = definition.getLength();
        if (255 < length) {
            if (16777215 < length) {
                sqlBuilder.append(" LONGTEXT");
            } else if (65535 < length) {
                sqlBuilder.append(" MEDIUMTEXT");
            } else {
                sqlBuilder.append(" TEXT");
            }
        } else {
            sqlBuilder.append(" VARCHAR(").append(length < 1 ? 255 : length);
            sqlBuilder.append(")");
        }

        if (!definition.getNullable()) {
            sqlBuilder.append(" NOT NULL");
        }
        return sqlBuilder.toString();
    }
}
